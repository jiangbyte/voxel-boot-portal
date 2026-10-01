package io.github.jiangbyte.voxel.boot.portal.infrastructure.storage;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ObjectStoragePort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ResolvedStorageSettings;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.StorageSettingsPort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * S3 兼容对象存储实现：put/get/delete/publicUrl/presign。
 *
 * Author: Charlie
 */
@Component
public class ObjectStoragePortImpl implements ObjectStoragePort {

    @Override
    public String put(ResolvedStorageSettings settings, String objectKey, InputStream inputStream,
                      long contentLength, String contentType) {
        // 1. 打开客户端并 PutObject
        try (S3ClientFactory.ManagedS3Clients clients = S3ClientFactory.create(settings)) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(settings.bucket())
                    .key(normalizeKey(objectKey))
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();
            clients.client().putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
            // 2. 返回即时可访问提示 URL
            return publicUrl(settings, objectKey);
        } catch (DomainException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            String detail = exception.getMessage() == null
                    ? exception.getClass().getSimpleName()
                    : exception.getMessage();
            throw new DomainException(500, "Failed to store object to S3: " + detail);
        }
    }

    @Override
    public void delete(ResolvedStorageSettings settings, String objectKey) {
        // 1. 幂等删除：NoSuchKey/404 视为成功
        try (S3ClientFactory.ManagedS3Clients clients = S3ClientFactory.create(settings)) {
            clients.client().deleteObject(DeleteObjectRequest.builder()
                    .bucket(settings.bucket())
                    .key(normalizeKey(objectKey))
                    .build());
        } catch (NoSuchKeyException exception) {
            // 已不存在
        } catch (S3Exception exception) {
            if (isNotFound(exception)) {
                return;
            }
            throw new DomainException(500, "Failed to delete S3 object: " + describe(exception));
        } catch (DomainException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            String detail = exception.getMessage() == null
                    ? exception.getClass().getSimpleName()
                    : exception.getMessage();
            throw new DomainException(500, "Failed to delete S3 object: " + detail);
        }
    }

    @Override
    public InputStream get(ResolvedStorageSettings settings, String objectKey) {
        // 1. 打开客户端并 GetObject（调用方负责关闭流；客户端在流关闭后仍可能被 GC）
        // 注意：流依赖客户端存活，因此此处读取为 ResponseInputStream，由调用方消费完后关闭。
        // 为避免客户端过早 close，使用不自动关闭的托管：将字节读出不合适；改为延迟关闭模式。
        S3ClientFactory.ManagedS3Clients clients = S3ClientFactory.create(settings);
        try {
            InputStream stream = clients.client().getObject(GetObjectRequest.builder()
                    .bucket(settings.bucket())
                    .key(normalizeKey(objectKey))
                    .build());
            return new ClientBoundInputStream(stream, clients);
        } catch (RuntimeException exception) {
            clients.close();
            throw new DomainException(404, "File not found");
        }
    }

    @Override
    public String publicUrl(ResolvedStorageSettings settings, String objectKey) {
        // 1. 公开桶：永久直链；私有桶：默认 TTL 预签名
        String key = normalizeKey(objectKey);
        if (settings.bucketPublic()) {
            return buildPublicDirectUrl(settings, key);
        }
        int expireSeconds = settings.presignExpireSeconds() > 0
                ? settings.presignExpireSeconds()
                : StorageSettingsPort.DEFAULT_PRESIGN_EXPIRE_SECONDS;
        return presignedUrl(settings, key, Duration.ofSeconds(expireSeconds));
    }

    @Override
    public String presignedUrl(ResolvedStorageSettings settings, String objectKey, Duration ttl) {
        // 1. 生成预签名；私有桶不做 host 改写
        try (S3ClientFactory.ManagedS3Clients clients = S3ClientFactory.create(settings)) {
            Duration expire = ttl == null || ttl.isNegative() || ttl.isZero()
                    ? Duration.ofMinutes(15)
                    : ttl;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(settings.bucket())
                    .key(normalizeKey(objectKey))
                    .build();
            PresignedGetObjectRequest presigned = clients.presigner().presignGetObject(
                    GetObjectPresignRequest.builder()
                            .signatureDuration(expire)
                            .getObjectRequest(getObjectRequest)
                            .build());
            String signed = presigned.url().toExternalForm();
            return rewritePublicHost(settings, signed);
        } catch (DomainException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new DomainException(500, "Failed to create S3 presigned URL");
        }
    }

    private static String buildPublicDirectUrl(ResolvedStorageSettings settings, String key) {
        if (StringUtils.hasText(settings.baseUrl())) {
            return joinBaseAndKey(settings.baseUrl(), key);
        }
        return buildEndpointObjectUrl(settings, key);
    }

    private static String buildEndpointObjectUrl(ResolvedStorageSettings settings, String key) {
        String bucket = settings.bucket();
        String endpoint = settings.endpoint();
        if (!StringUtils.hasText(bucket) || !StringUtils.hasText(endpoint)) {
            throw new DomainException(500, "S3 bucket/endpoint is required for public URL");
        }
        String encodedKey = quoteObjectKey(key);
        String origin = normalizeEndpointOrigin(endpoint, settings.useSsl());
        if (settings.pathStyleAccess()) {
            return origin + "/" + bucket + "/" + encodedKey;
        }
        URI uri = URI.create(origin);
        String host = uri.getRawAuthority();
        if (!StringUtils.hasText(host)) {
            return origin + "/" + bucket + "/" + encodedKey;
        }
        String scheme = uri.getScheme() == null ? "https" : uri.getScheme();
        return scheme + "://" + bucket + "." + host + "/" + encodedKey;
    }

    private static String normalizeEndpointOrigin(String endpoint, boolean useSsl) {
        String value = endpoint.trim();
        if (!value.contains("://")) {
            value = (useSsl ? "https://" : "http://") + value;
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static String joinBaseAndKey(String baseUrl, String key) {
        String base = baseUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + quoteObjectKey(key);
    }

    private static String quoteObjectKey(String objectKey) {
        return Arrays.stream(objectKey.replace('\\', '/').split("/"))
                .filter(StringUtils::hasText)
                .map(part -> URLEncoder.encode(part, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }

    private static String rewritePublicHost(ResolvedStorageSettings settings, String signedUrl) {
        // 私有桶不做 host 改写，避免 SigV4 SignatureDoesNotMatch
        if (!settings.bucketPublic()) {
            return signedUrl;
        }
        String publicBaseUrl = settings.baseUrl();
        if (!StringUtils.hasText(publicBaseUrl)) {
            return signedUrl;
        }
        try {
            URI signed = URI.create(signedUrl);
            URI pub = URI.create(publicBaseUrl.endsWith("/")
                    ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                    : publicBaseUrl);
            StringBuilder rebuilt = new StringBuilder();
            rebuilt.append(pub.getScheme() != null ? pub.getScheme() : signed.getScheme()).append("://");
            rebuilt.append(pub.getRawAuthority() != null ? pub.getRawAuthority() : signed.getRawAuthority());
            if (StringUtils.hasText(pub.getRawPath()) && !"/".equals(pub.getRawPath())) {
                String basePath = pub.getRawPath().endsWith("/")
                        ? pub.getRawPath().substring(0, pub.getRawPath().length() - 1)
                        : pub.getRawPath();
                String objectPath = signed.getRawPath() == null ? "" : signed.getRawPath();
                rebuilt.append(basePath).append(objectPath);
            } else {
                rebuilt.append(signed.getRawPath() == null ? "" : signed.getRawPath());
            }
            if (StringUtils.hasText(signed.getRawQuery())) {
                rebuilt.append('?').append(signed.getRawQuery());
            }
            return rebuilt.toString();
        } catch (IllegalArgumentException ignored) {
            return signedUrl;
        }
    }

    private static boolean isNotFound(S3Exception exception) {
        int status = exception.statusCode();
        if (status == 404) {
            return true;
        }
        String code = exception.awsErrorDetails() == null
                ? null
                : exception.awsErrorDetails().errorCode();
        return code != null && (code.equalsIgnoreCase("NoSuchKey")
                || code.equalsIgnoreCase("NoSuchBucket")
                || code.equalsIgnoreCase("NotFound")
                || code.equalsIgnoreCase("NoSuchObject"));
    }

    private static String describe(S3Exception exception) {
        String code = exception.awsErrorDetails() == null
                ? null
                : exception.awsErrorDetails().errorCode();
        String message = exception.getMessage() == null
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
        return code == null ? message : code + " (" + exception.statusCode() + "): " + message;
    }

    private static String normalizeKey(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return "";
        }
        return objectKey.replace('\\', '/').replaceAll("^/+", "");
    }

    /**
     * 绑定客户端生命周期到对象流：流关闭时一并关闭 S3 客户端。
     */
    private static final class ClientBoundInputStream extends InputStream {
        private final InputStream delegate;
        private final AutoCloseable clients;

        private ClientBoundInputStream(InputStream delegate, AutoCloseable clients) {
            this.delegate = delegate;
            this.clients = clients;
        }

        @Override
        public int read() throws java.io.IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws java.io.IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public void close() throws java.io.IOException {
            try {
                delegate.close();
            } finally {
                try {
                    clients.close();
                } catch (Exception ignored) {
                    // 忽略
                }
            }
        }
    }
}

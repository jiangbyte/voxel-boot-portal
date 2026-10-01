package io.github.jiangbyte.voxel.boot.portal.infrastructure.storage;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ResolvedStorageSettings;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * S3 客户端工厂：按 ResolvedStorageSettings 构建 path-style 兼容客户端。
 *
 * Author: Charlie
 */
public final class S3ClientFactory {

    private S3ClientFactory() {
    }

    /**
     * 创建可关闭的 S3 客户端与预签名器对。
     */
    public static ManagedS3Clients create(ResolvedStorageSettings settings) {
        // 1. 校验必要配置
        if (settings == null) {
            throw new DomainException(500, "S3 storage settings required");
        }
        if (!StringUtils.hasText(settings.bucket())) {
            throw new DomainException(500, "Storage engine missing: bucket");
        }
        if (!StringUtils.hasText(settings.accessKey()) || !StringUtils.hasText(settings.secretKey())) {
            throw new DomainException(500, "Storage engine missing ACCESS_KEY/SECRET_KEY");
        }
        // 2. 组装凭证与区域
        String region = StringUtils.hasText(settings.region()) ? settings.region().trim() : "us-east-1";
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(settings.accessKey().trim(), settings.secretKey().trim()));
        // 3. 构建 S3Client（MinIO/RUSTFS path-style）
        S3ClientBuilder clientBuilder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentials);
        if (StringUtils.hasText(settings.endpoint())) {
            clientBuilder.endpointOverride(normalizeEndpoint(settings.endpoint(), settings.useSsl()));
        }
        if (settings.pathStyleAccess()) {
            clientBuilder.forcePathStyle(true);
        }
        S3Client client = clientBuilder.build();
        // 4. 构建 S3Presigner
        S3Presigner.Builder presignerBuilder = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentials);
        if (StringUtils.hasText(settings.endpoint())) {
            presignerBuilder.endpointOverride(normalizeEndpoint(settings.endpoint(), settings.useSsl()));
        }
        if (settings.pathStyleAccess()) {
            presignerBuilder.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }
        S3Presigner presigner = presignerBuilder.build();
        return new ManagedS3Clients(client, presigner);
    }

    private static URI normalizeEndpoint(String endpoint, boolean useSsl) {
        String value = endpoint.trim();
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = (useSsl ? "https://" : "http://") + value;
        }
        return URI.create(value);
    }

    /**
     * 成对管理 S3Client / S3Presigner 生命周期。
     */
    public static final class ManagedS3Clients implements AutoCloseable {
        private final S3Client client;
        private final S3Presigner presigner;

        ManagedS3Clients(S3Client client, S3Presigner presigner) {
            this.client = client;
            this.presigner = presigner;
        }

        public S3Client client() {
            return client;
        }

        public S3Presigner presigner() {
            return presigner;
        }

        @Override
        public void close() {
            try {
                presigner.close();
            } catch (Exception ignored) {
                // 忽略关闭异常
            }
            try {
                client.close();
            } catch (Exception ignored) {
                // 忽略关闭异常
            }
        }
    }
}

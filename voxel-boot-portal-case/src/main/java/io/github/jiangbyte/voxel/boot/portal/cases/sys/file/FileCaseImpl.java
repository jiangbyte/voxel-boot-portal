package io.github.jiangbyte.voxel.boot.portal.cases.sys.file;

import cn.hutool.core.util.IdUtil;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileAsset;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileObjectKeys;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileAccessUrlPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileDownloadPayload;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileUrlView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ObjectStoragePort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ResolvedStorageSettings;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.StorageSettingsPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 文件写用例：真实对象存储上传/下载/删除，读路径解析访问 URL。
 *
 * Author: Charlie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileCaseImpl implements IFileCase {

    private final FileRepository fileRepository;
    private final StorageSettingsPort storageSettingsPort;
    private final ObjectStoragePort objectStoragePort;
    private final FileAccessUrlPort fileAccessUrlPort;
    private final ConfigApi configApi;

    /**
     * 登记文件元数据。
     */
    @Transactional
    public FileView register(CreateFileCommand command) {
        // 1. objectName 唯一校验
        fileRepository.findByObjectName(command.objectName()).ifPresent(existing -> {
            throw new DomainException("File objectName already exists");
        });
        // 2. 工厂创建聚合
        FileAsset file = FileAsset.create(
                command.objectName(),
                command.originalName(),
                command.storageProvider(),
                command.bucket(),
                command.contentType(),
                command.size(),
                command.url());
        // 3. 持久化并返回已解析 URL 的视图
        fileRepository.save(file);
        return withResolvedUrl(toView(file));
    }

    /**
     * 真实上传：校验 → put → 落库 object key → 读时解析 URL。
     */
    @Transactional
    public FileView upload(String originalName, String contentType, long size, InputStream inputStream,
                           String storageProvider) {
        // 1. 入参与流校验
        if (inputStream == null || !StringUtils.hasText(originalName)) {
            throw new DomainException("File is required");
        }
        // 2. 解析存储配置并校验大小/扩展名/Content-Type
        ResolvedStorageSettings settings = storageSettingsPort.resolve(storageProvider);
        validateUpload(originalName, contentType, size, settings);
        String safeName = safeOriginalName(originalName);
        String objectName = buildObjectName(safeName, "uploads");
        String sanitizedType = sanitizeContentType(contentType);
        // 3. put 到对象存储（落库不存预签名）
        objectStoragePort.put(settings, objectName, inputStream, size, sanitizedType);
        // 4. 登记元数据：url 列 = object key
        return register(new CreateFileCommand(
                objectName,
                safeName,
                settings.provider(),
                settings.bucket(),
                sanitizedType,
                size,
                objectName));
    }

    /**
     * 更新原始文件名。
     */
    @Transactional
    public void update(UpdateFileCommand command) {
        // 1. 加载聚合
        FileAsset file = fileRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "File not found"));
        // 2. 重命名并保存
        file.rename(command.originalName());
        fileRepository.save(file);
    }

    /**
     * 批量删除：先删存储对象（失败仅 warn），再删库。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个：删存储 → 删库
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            fileRepository.findById(id).ifPresent(file -> {
                String objectKey = FileObjectKeys.toObjectKey(file.getObjectName());
                if (objectKey != null) {
                    try {
                        ResolvedStorageSettings settings = storageSettingsPort.resolve(file.getStorageProvider());
                        objectStoragePort.delete(settings, objectKey);
                    } catch (Exception ex) {
                        log.warn("Failed to delete storage object, skip (id={}, object={}, provider={}): {}",
                                file.getId(), objectKey, file.getStorageProvider(), ex.getMessage());
                    }
                }
                file.markDeleted();
                fileRepository.remove(file);
            });
        }
    }

    /**
     * 按 id 加载对象字节流。
     */
    @Override
    public FileDownloadPayload download(String id) {
        // 1. 加载元数据
        FileAsset file = fileRepository.findById(id)
                .orElseThrow(() -> new DomainException(404, "File not found"));
        // 2. 规范化 object key
        String objectKey = FileObjectKeys.toObjectKey(file.getObjectName());
        if (objectKey == null) {
            throw new DomainException(404, "File not found");
        }
        // 3. 从存储加载流
        ResolvedStorageSettings settings = storageSettingsPort.resolve(file.getStorageProvider());
        InputStream stream = objectStoragePort.get(settings, objectKey);
        return new FileDownloadPayload(stream, file.getContentType(), file.getOriginalName(), file.getSize());
    }

    /**
     * 按 objectName 解析公开访问 URL。
     */
    @Override
    public FileUrlView resolvePublicUrl(String objectName) {
        // 1. 规范化 key
        String objectKey = FileObjectKeys.toObjectKey(objectName);
        if (objectKey == null) {
            throw new DomainException(404, "File not found");
        }
        // 2. 查元数据（可无记录，用默认引擎）
        FileAsset file = fileRepository.findByObjectName(objectKey).orElse(null);
        String provider = file != null ? file.getStorageProvider() : null;
        ResolvedStorageSettings settings = storageSettingsPort.resolve(provider);
        String url = objectStoragePort.publicUrl(settings, objectKey);
        return toUrlView(objectKey, url, file);
    }

    /**
     * 按 objectName 生成预签名 URL。
     */
    @Override
    public FileUrlView resolvePresignedUrl(String objectName) {
        // 1. 规范化 key
        String objectKey = FileObjectKeys.toObjectKey(objectName);
        if (objectKey == null) {
            throw new DomainException(404, "File not found");
        }
        // 2. 解析 TTL 与 provider
        FileAsset file = fileRepository.findByObjectName(objectKey).orElse(null);
        String provider = file != null ? file.getStorageProvider() : null;
        ResolvedStorageSettings settings = storageSettingsPort.resolve(provider);
        Duration ttl = Duration.ofSeconds(Math.max(1, settings.presignExpireSeconds()));
        String url = objectStoragePort.presignedUrl(settings, objectKey, ttl);
        return toUrlView(objectKey, url, file);
    }

    private void validateUpload(String originalName, String contentType, long size,
                                ResolvedStorageSettings settings) {
        // 1. 大小
        long maxBytes = settings.uploadMaxBytes() > 0
                ? settings.uploadMaxBytes()
                : StorageSettingsPort.DEFAULT_UPLOAD_MAX_BYTES;
        if (size > maxBytes) {
            throw new DomainException(400, "File exceeds max size");
        }
        // 2. 扩展名黑白名单
        String ext = extensionOf(originalName);
        List<String> denied = readJsonStringList(configApi.getByKey("STORAGE_UPLOAD_DENIED_EXTENSIONS", "[]"));
        if (!denied.isEmpty() && denied.stream().anyMatch(e -> normalizeExt(e).equals(ext))) {
            throw new DomainException(400, "File extension not allowed: " + ext);
        }
        List<String> allowedExt = readJsonStringList(configApi.getByKey("STORAGE_UPLOAD_ALLOWED_EXTENSIONS", "[]"));
        if (!allowedExt.isEmpty() && allowedExt.stream().noneMatch(e -> normalizeExt(e).equals(ext))) {
            throw new DomainException(400, "File extension not allowed: " + ext);
        }
        // 3. Content-Type 白名单
        List<String> allowedTypes = readJsonStringList(configApi.getByKey("STORAGE_UPLOAD_ALLOWED_CONTENT_TYPES", "[]"));
        if (StringUtils.hasText(contentType)
                && !allowedTypes.isEmpty()
                && allowedTypes.stream().noneMatch(t -> matchesContentType(contentType, t))) {
            throw new DomainException(400, "Content type not allowed: " + contentType);
        }
    }

    private FileView withResolvedUrl(FileView view) {
        if (view == null || !StringUtils.hasText(view.objectName())) {
            return view;
        }
        String resolved = fileAccessUrlPort.resolveAccessUrl(view.objectName(), view.storageProvider());
        if (!StringUtils.hasText(resolved)) {
            return view;
        }
        return new FileView(
                view.id(),
                view.objectName(),
                view.originalName(),
                view.storageProvider(),
                view.bucket(),
                view.contentType(),
                view.size(),
                resolved,
                view.createdBy(),
                view.createdAt(),
                view.updatedAt());
    }

    private static FileView toView(FileAsset file) {
        return new FileView(
                file.getId(),
                file.getObjectName(),
                file.getOriginalName(),
                file.getStorageProvider(),
                file.getBucket(),
                file.getContentType(),
                file.getSize(),
                file.getUrl(),
                null,
                null,
                null);
    }

    private static FileUrlView toUrlView(String objectKey, String url, FileAsset file) {
        if (file == null) {
            return new FileUrlView(objectKey, url, null, null, null);
        }
        return new FileUrlView(
                objectKey,
                url,
                file.getOriginalName(),
                file.getSize(),
                file.getContentType());
    }

    private static String buildObjectName(String filename, String category) {
        String safeCategory = StringUtils.hasText(category) ? category.trim() : "uploads";
        String safeName = filename.replace("\\", "/");
        int slash = safeName.lastIndexOf('/');
        if (slash >= 0) {
            safeName = safeName.substring(slash + 1);
        }
        int dot = safeName.lastIndexOf('.');
        String suffix = dot >= 0 ? safeName.substring(dot).toLowerCase(Locale.ROOT) : "";
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return "%s/%04d/%02d/%02d/%s%s".formatted(
                safeCategory,
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                IdUtil.simpleUUID(),
                suffix);
    }

    private static String safeOriginalName(String filename) {
        String safe = filename.replace("\\", "/");
        int slash = safe.lastIndexOf('/');
        if (slash >= 0) {
            safe = safe.substring(slash + 1);
        }
        return safe.isBlank() ? "file" : safe;
    }

    private static String sanitizeContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "application/octet-stream";
        }
        return contentType.trim();
    }

    private static String extensionOf(String filename) {
        String safe = filename.replace("\\", "/");
        int slash = safe.lastIndexOf('/');
        if (slash >= 0) {
            safe = safe.substring(slash + 1);
        }
        int dot = safe.lastIndexOf('.');
        return dot >= 0 ? safe.substring(dot).toLowerCase(Locale.ROOT) : "";
    }

    private static String normalizeExt(String ext) {
        if (ext == null) {
            return "";
        }
        String value = ext.trim().toLowerCase(Locale.ROOT);
        return value.startsWith(".") ? value : "." + value;
    }

    private static boolean matchesContentType(String contentType, String allowed) {
        if (!StringUtils.hasText(contentType) || !StringUtils.hasText(allowed)) {
            return false;
        }
        String normalizedType = contentType.trim();
        String normalizedAllowed = allowed.trim();
        if (normalizedAllowed.endsWith("/*")) {
            String prefix = normalizedAllowed.substring(0, normalizedAllowed.length() - 1);
            return normalizedType.regionMatches(true, 0, prefix, 0, prefix.length());
        }
        return normalizedAllowed.equalsIgnoreCase(normalizedType);
    }

    /**
     * 解析 JSON 字符串数组（仅支持简单 ["a","b"]）；失败返回空列表。
     */
    private static List<String> readJsonStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        String trimmed = json.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return List.of();
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < body.length(); i++) {
            char ch = body.charAt(i);
            if (ch == '"') {
                inQuote = !inQuote;
                continue;
            }
            if (ch == ',' && !inQuote) {
                String item = current.toString().trim();
                if (!item.isEmpty()) {
                    result.add(item);
                }
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        String last = current.toString().trim();
        if (!last.isEmpty()) {
            result.add(last);
        }
        return result;
    }
}

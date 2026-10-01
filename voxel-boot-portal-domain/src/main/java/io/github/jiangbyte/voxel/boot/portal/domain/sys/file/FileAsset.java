package io.github.jiangbyte.voxel.boot.portal.domain.sys.file;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.event.FileCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.event.FileDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.event.FileUpdated;

import java.util.UUID;

/**
 * 文件元数据聚合根：维护 objectName、原始名、大小与访问 URL 等不变量。
 *
 * Author: Charlie
 */
public class FileAsset extends AggregateRoot<String> {

    private String id;
    private String objectName;
    private String originalName;
    private String storageProvider;
    private String bucket;
    private String contentType;
    private Long size;
    private String url;

    private FileAsset() {
    }

    /**
     * 登记上传后的文件元数据。
     */
    public static FileAsset create(String objectName,
                                   String originalName,
                                   String storageProvider,
                                   String bucket,
                                   String contentType,
                                   Long size,
                                   String url) {
        // 1. 校验存储键与原始名
        String normalizedObject = requireObjectName(objectName);
        String normalizedOriginal = requireOriginalName(originalName);
        // 2. 校验大小非负
        long normalizedSize = requireSize(size);
        // 3. 组装聚合
        FileAsset file = new FileAsset();
        file.id = UUID.randomUUID().toString().replace("-", "");
        file.objectName = normalizedObject;
        file.originalName = normalizedOriginal;
        file.storageProvider = blankToDefault(storageProvider, "local");
        file.bucket = blankToNull(bucket);
        file.contentType = blankToDefault(contentType, "application/octet-stream");
        file.size = normalizedSize;
        file.url = blankToDefault(url, normalizedObject);
        // 4. 登记领域事件
        file.registerEvent(new FileCreated(file.id, file.objectName));
        return file;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static FileAsset reconstitute(String id,
                                         String objectName,
                                         String originalName,
                                         String storageProvider,
                                         String bucket,
                                         String contentType,
                                         Long size,
                                         String url) {
        FileAsset file = new FileAsset();
        file.id = id;
        file.objectName = objectName;
        file.originalName = originalName;
        file.storageProvider = storageProvider;
        file.bucket = bucket;
        file.contentType = contentType;
        file.size = size == null ? 0L : size;
        file.url = url;
        return file;
    }

    /**
     * 更新展示用原始文件名。
     */
    public void rename(String originalName) {
        // 1. 校验新名称
        this.originalName = requireOriginalName(originalName);
        // 2. 登记更新事件
        registerEvent(new FileUpdated(this.id, this.objectName));
    }

    /**
     * 标记删除。
     */
    public void markDeleted() {
        registerEvent(new FileDeleted(this.id, this.objectName));
    }

    private static String requireObjectName(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            throw new DomainException("File objectName must not be blank");
        }
        return objectName.trim();
    }

    private static String requireOriginalName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            throw new DomainException("File originalName must not be blank");
        }
        return originalName.trim();
    }

    private static long requireSize(Long size) {
        if (size == null || size < 0) {
            throw new DomainException("File size must be >= 0");
        }
        return size;
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    private static String blankToDefault(String text, String defaultValue) {
        return text == null || text.isBlank() ? defaultValue : text.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public String getBucket() {
        return bucket;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }
}

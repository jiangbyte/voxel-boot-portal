package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

import java.time.LocalDateTime;

/**
 * 文件只读视图。
 *
 * Author: Charlie
 */
public record FileView(
        String id,
        String objectName,
        String originalName,
        String storageProvider,
        String bucket,
        String contentType,
        Long size,
        String url,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

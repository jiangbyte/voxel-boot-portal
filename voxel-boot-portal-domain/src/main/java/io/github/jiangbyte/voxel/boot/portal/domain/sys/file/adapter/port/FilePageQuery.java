package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

/**
 * 文件分页查询。
 *
 * Author: Charlie
 */
public record FilePageQuery(
        long current,
        long size,
        String originalName,
        String storageProvider,
        String objectName
) {
}

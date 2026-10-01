package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

/**
 * 跨上下文文件快照。
 *
 * Author: Charlie
 */
public record FileInfo(
        String id,
        String objectName,
        String originalName,
        String contentType,
        Long size,
        String url
) {
}

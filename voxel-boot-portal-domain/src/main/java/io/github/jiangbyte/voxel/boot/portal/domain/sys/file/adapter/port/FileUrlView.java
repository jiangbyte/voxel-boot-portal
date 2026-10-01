package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

/**
 * 文件访问 URL 视图。
 *
 * Author: Charlie
 */
public record FileUrlView(
        String objectName,
        String url,
        String originalName,
        Long size,
        String contentType
) {
}

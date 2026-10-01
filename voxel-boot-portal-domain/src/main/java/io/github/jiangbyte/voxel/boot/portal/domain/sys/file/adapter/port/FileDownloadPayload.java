package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

import java.io.InputStream;

/**
 * 文件下载载荷：流 + 元数据。
 *
 * Author: Charlie
 */
public record FileDownloadPayload(
        InputStream stream,
        String contentType,
        String originalName,
        Long size
) {
}

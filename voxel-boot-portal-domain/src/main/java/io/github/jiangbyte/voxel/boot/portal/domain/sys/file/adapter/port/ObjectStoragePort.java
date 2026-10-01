package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

import java.io.InputStream;
import java.time.Duration;

/**
 * 对象存储端口：S3 兼容 put/get/delete 与访问 URL。
 *
 * Author: Charlie
 */
public interface ObjectStoragePort {

    /**
     * 上传对象；成功返回可访问提示 URL（落库仍用 object key）。
     */
    String put(ResolvedStorageSettings settings, String objectKey, InputStream inputStream,
               long contentLength, String contentType);

    /**
     * 删除对象（对象不存在视为成功）。
     */
    void delete(ResolvedStorageSettings settings, String objectKey);

    /**
     * 加载对象字节流。
     */
    InputStream get(ResolvedStorageSettings settings, String objectKey);

    /**
     * 公开直链或默认预签名访问 URL。
     */
    String publicUrl(ResolvedStorageSettings settings, String objectKey);

    /**
     * 带 TTL 的预签名 GET URL。
     */
    String presignedUrl(ResolvedStorageSettings settings, String objectKey, Duration ttl);
}

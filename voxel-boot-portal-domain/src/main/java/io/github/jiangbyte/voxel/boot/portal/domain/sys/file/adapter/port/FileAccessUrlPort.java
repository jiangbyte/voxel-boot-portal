package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

/**
 * 文件访问 URL 解析端口：object key / 外链 / 脏预签 URL → 可浏览器访问地址。
 *
 * Author: Charlie
 */
public interface FileAccessUrlPort {

    /**
     * 是否已是 http(s)/data/blob 外链。
     */
    boolean isExternalUrl(String value);

    /**
     * 是否疑似预签名临时 URL。
     */
    boolean looksLikePresignedUrl(String value);

    /**
     * 规范化为纯 object key（外链则抽 path）。
     */
    String toObjectKey(String value);

    /**
     * 解析可访问 URL（永久外链原样；否则按 provider 生成 publicUrl）。
     */
    String resolveAccessUrl(String objectNameOrUrl, String storageProvider);
}

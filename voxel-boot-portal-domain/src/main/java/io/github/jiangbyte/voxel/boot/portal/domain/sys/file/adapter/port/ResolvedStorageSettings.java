package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

/**
 * 已解析的对象存储运行时配置（来自 sys_config）。
 *
 * Author: Charlie
 */
public record ResolvedStorageSettings(
        String provider,
        String engine,
        String bucket,
        String endpoint,
        String accessKey,
        String secretKey,
        String region,
        boolean useSsl,
        boolean pathStyleAccess,
        String baseUrl,
        boolean bucketPublic,
        long uploadMaxBytes,
        int presignExpireSeconds
) {
}

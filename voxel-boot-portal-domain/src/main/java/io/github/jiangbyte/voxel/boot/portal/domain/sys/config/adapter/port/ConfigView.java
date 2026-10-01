package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port;

import java.time.LocalDateTime;

/**
 * 系统配置只读视图。
 *
 * Author: Charlie
 */
public record ConfigView(
        String id,
        String configKey,
        String configValue,
        String category,
        String remark,
        Integer sortCode,
        String valueType,
        String label,
        String scope,
        String scene,
        Integer builtin,
        Integer sensitive,
        String extJson,
        Integer version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

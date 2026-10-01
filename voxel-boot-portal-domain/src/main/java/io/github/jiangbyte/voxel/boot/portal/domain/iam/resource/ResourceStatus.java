package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 资源启用状态。
 *
 * Author: Charlie
 */
public enum ResourceStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static ResourceStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return ResourceStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Resource status must be ENABLED or DISABLED");
        }
    }
}

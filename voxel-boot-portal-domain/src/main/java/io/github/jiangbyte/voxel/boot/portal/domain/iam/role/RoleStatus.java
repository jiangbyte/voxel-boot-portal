package io.github.jiangbyte.voxel.boot.portal.domain.iam.role;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 角色启用状态。
 *
 * Author: Charlie
 */
public enum RoleStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static RoleStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return RoleStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Role status must be ENABLED or DISABLED");
        }
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.group;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 用户组启用状态。
 *
 * Author: Charlie
 */
public enum GroupStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static GroupStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return GroupStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Group status must be ENABLED or DISABLED");
        }
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 部门启用状态。
 *
 * Author: Charlie
 */
public enum DeptStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static DeptStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return DeptStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Dept status must be ENABLED or DISABLED");
        }
    }
}

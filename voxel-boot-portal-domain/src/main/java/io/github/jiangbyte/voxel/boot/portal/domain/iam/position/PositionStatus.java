package io.github.jiangbyte.voxel.boot.portal.domain.iam.position;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 岗位启用状态。
 *
 * Author: Charlie
 */
public enum PositionStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static PositionStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return PositionStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Position status must be ENABLED or DISABLED");
        }
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

/**
 * 实名工单状态。
 *
 * Author: Charlie
 */
public enum RealNameCaseStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static RealNameCaseStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RealNameCaseStatus must not be blank");
        }
        return RealNameCaseStatus.valueOf(value.trim().toUpperCase());
    }
}

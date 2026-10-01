package io.github.jiangbyte.voxel.boot.portal.domain.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 账号状态：运行时使用 ENABLED；LOCKED/CANCELLED 与库表约定对齐。
 *
 * Author: Charlie
 */
public enum AccountStatus {

    ENABLED,
    LOCKED,
    CANCELLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static AccountStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        String normalized = raw.trim().toUpperCase();
        // 兼容历史注释/文档中的 ACTIVE 写法
        if ("ACTIVE".equals(normalized)) {
            return ENABLED;
        }
        try {
            return AccountStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Account status must be ENABLED, LOCKED or CANCELLED");
        }
    }
}

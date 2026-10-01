package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 反馈处理状态（与字典 FEEDBACK_STATUS 及存量数据对齐；REPLIED 兼容 boot 文案）。
 *
 * Author: Charlie
 */
public enum FeedbackStatus {

    PENDING,
    REVIEWED,
    REPLIED,
    RESOLVED,
    CLOSED;

    /**
     * 解析状态；空值默认 PENDING。
     */
    public static FeedbackStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return PENDING;
        }
        try {
            return FeedbackStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Feedback status must be PENDING, REVIEWED, REPLIED, RESOLVED or CLOSED");
        }
    }
}

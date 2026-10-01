package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 公告发布状态。
 *
 * Author: Charlie
 */
public enum NoticeStatus {

    DRAFT,
    PUBLISHED,
    REVOKED;

    /**
     * 解析状态字符串；空值默认 DRAFT。
     */
    public static NoticeStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return DRAFT;
        }
        try {
            return NoticeStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Notice status must be DRAFT, PUBLISHED or REVOKED");
        }
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 消息种类。
 *
 * Author: Charlie
 */
public enum NoticeKind {

    NOTIFICATION,
    ANNOUNCEMENT;

    /**
     * 解析种类；空值默认 ANNOUNCEMENT。
     */
    public static NoticeKind from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ANNOUNCEMENT;
        }
        try {
            return NoticeKind.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Notice kind must be NOTIFICATION or ANNOUNCEMENT");
        }
    }
}

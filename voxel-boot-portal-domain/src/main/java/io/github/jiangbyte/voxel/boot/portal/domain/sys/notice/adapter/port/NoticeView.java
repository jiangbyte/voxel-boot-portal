package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port;

import java.time.LocalDateTime;

/**
 * 公告只读视图。
 *
 * Author: Charlie
 */
public record NoticeView(
        String id,
        String kind,
        String title,
        String content,
        String contentType,
        String category,
        String severity,
        String targetScope,
        String targetAccountTypes,
        String targetAccountIds,
        String targetDeptIds,
        String targetRoleIds,
        String publishLocations,
        Integer pinned,
        LocalDateTime pinnedUntil,
        String senderAccountType,
        String senderAccountId,
        String sourceType,
        String sourceId,
        String status,
        LocalDateTime publishAt,
        LocalDateTime revokedAt,
        LocalDateTime expireAt,
        Integer viewCount,
        String extra,
        Integer isRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

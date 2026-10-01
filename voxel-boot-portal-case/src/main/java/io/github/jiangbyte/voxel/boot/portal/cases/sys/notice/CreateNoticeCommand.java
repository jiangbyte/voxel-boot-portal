package io.github.jiangbyte.voxel.boot.portal.cases.sys.notice;

import java.time.LocalDateTime;

/**
 * 创建公告命令。
 *
 * Author: Charlie
 */
public record CreateNoticeCommand(
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
        LocalDateTime expireAt,
        String extra
) {
}

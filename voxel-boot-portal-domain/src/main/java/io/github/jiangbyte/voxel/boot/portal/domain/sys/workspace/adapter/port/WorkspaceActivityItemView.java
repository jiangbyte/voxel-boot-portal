package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port;

import java.time.LocalDateTime;

/**
 * 工作台近期操作/登录日志摘要视图。
 *
 * Author: Charlie
 */
public record WorkspaceActivityItemView(
        String id,
        String module,
        String moduleLabel,
        String action,
        String actionName,
        String actionType,
        String summary,
        Integer success,
        String ip,
        String userAgent,
        String operatorName,
        Integer durationMs,
        String resourceId,
        LocalDateTime createdAt
) {
}

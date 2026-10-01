package io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作审计只读视图。
 *
 * Author: Charlie
 */
public record AuditView(
        String id,
        String module,
        String resourceType,
        String resourceId,
        String action,
        String summary,
        Map<String, Object> beforeData,
        Map<String, Object> afterData,
        String accountId,
        String accountType,
        String requestId,
        String ip,
        String userAgent,
        Integer success,
        String errorMessage,
        String operatorName,
        String actionName,
        String actionType,
        String moduleLabel,
        Integer durationMs,
        LocalDateTime createdAt
) {
}

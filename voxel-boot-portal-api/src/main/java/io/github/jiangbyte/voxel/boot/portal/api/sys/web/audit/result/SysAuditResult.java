package io.github.jiangbyte.voxel.boot.portal.api.sys.web.audit.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作审计响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "操作审计响应")
public class SysAuditResult {
    private String id;
    private String module;
    private String resourceType;
    private String resourceId;
    private String action;
    private String summary;
    private Map<String, Object> beforeData;
    private Map<String, Object> afterData;
    private String accountId;
    private String accountType;
    private String requestId;
    private String ip;
    private String userAgent;
    private Integer success;
    private String errorMessage;
    private String operatorName;
    private String actionName;
    private String actionType;
    private String moduleLabel;
    private Integer durationMs;
    private LocalDateTime createdAt;
}

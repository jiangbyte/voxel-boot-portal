package io.github.jiangbyte.voxel.boot.portal.domain.sys.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 操作审计日志实体：记录管理端写操作成功后的审计快照。
 *
 * Author: Charlie
 */
public class OperationAuditLog {

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

    private OperationAuditLog() {
    }

    /**
     * 构造一条成功写操作审计记录。
     */
    public static OperationAuditLog recordSuccess(String module,
                                                  String resourceType,
                                                  String resourceId,
                                                  String action,
                                                  String actionName,
                                                  String actionType,
                                                  String moduleLabel,
                                                  String summary,
                                                  String accountId,
                                                  String accountType,
                                                  String operatorName,
                                                  String requestId,
                                                  String ip,
                                                  String userAgent,
                                                  Integer durationMs) {
        // 1. 分配主键与时间
        OperationAuditLog log = new OperationAuditLog();
        log.id = UUID.randomUUID().toString().replace("-", "");
        log.createdAt = LocalDateTime.now();
        // 2. 填充业务与操作人字段
        log.module = module;
        log.resourceType = resourceType;
        log.resourceId = resourceId;
        log.action = action;
        log.actionName = actionName;
        log.actionType = actionType;
        log.moduleLabel = moduleLabel;
        log.summary = summary;
        log.accountId = accountId;
        log.accountType = accountType;
        log.operatorName = operatorName;
        log.requestId = requestId;
        log.ip = ip;
        log.userAgent = userAgent;
        log.durationMs = durationMs;
        // 3. 标记成功
        log.success = 1;
        return log;
    }

    public String getId() { return id; }
    public String getModule() { return module; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getAction() { return action; }
    public String getSummary() { return summary; }
    public Map<String, Object> getBeforeData() { return beforeData; }
    public Map<String, Object> getAfterData() { return afterData; }
    public String getAccountId() { return accountId; }
    public String getAccountType() { return accountType; }
    public String getRequestId() { return requestId; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
    public Integer getSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public String getOperatorName() { return operatorName; }
    public String getActionName() { return actionName; }
    public String getActionType() { return actionType; }
    public String getModuleLabel() { return moduleLabel; }
    public Integer getDurationMs() { return durationMs; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** 仓储重建。 */
    public static OperationAuditLog rehydrate(String id, String module, String resourceType, String resourceId,
                                             String action, String summary, Map<String, Object> beforeData,
                                             Map<String, Object> afterData, String accountId, String accountType,
                                             String requestId, String ip, String userAgent, Integer success,
                                             String errorMessage, String operatorName, String actionName,
                                             String actionType, String moduleLabel, Integer durationMs,
                                             LocalDateTime createdAt) {
        OperationAuditLog log = new OperationAuditLog();
        log.id = id;
        log.module = module;
        log.resourceType = resourceType;
        log.resourceId = resourceId;
        log.action = action;
        log.summary = summary;
        log.beforeData = beforeData;
        log.afterData = afterData;
        log.accountId = accountId;
        log.accountType = accountType;
        log.requestId = requestId;
        log.ip = ip;
        log.userAgent = userAgent;
        log.success = success;
        log.errorMessage = errorMessage;
        log.operatorName = operatorName;
        log.actionName = actionName;
        log.actionType = actionType;
        log.moduleLabel = moduleLabel;
        log.durationMs = durationMs;
        log.createdAt = createdAt;
        return log;
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.audit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作审计日志 PO，对应表 sys_operation_audit_log。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "sys_operation_audit_log", autoResultMap = true)
public class OperationAuditLogPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String module;
    private String resourceType;
    private String resourceId;
    private String action;
    private String summary;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> beforeData;

    @TableField(typeHandler = JacksonTypeHandler.class)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

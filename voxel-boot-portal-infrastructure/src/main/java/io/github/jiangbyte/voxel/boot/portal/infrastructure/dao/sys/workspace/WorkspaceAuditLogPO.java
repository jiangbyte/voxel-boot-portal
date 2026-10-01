package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作台只读审计日志投影，对应表 sys_operation_audit_log（仅查询所需字段）。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_operation_audit_log")
public class WorkspaceAuditLogPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String module;

    private String action;

    private String summary;

    private String accountId;

    private String accountType;

    private String ip;

    private String userAgent;

    private Integer success;

    private String operatorName;

    private String actionName;

    private String actionType;

    private String moduleLabel;

    private Integer durationMs;

    private String resourceId;

    private LocalDateTime createdAt;
}

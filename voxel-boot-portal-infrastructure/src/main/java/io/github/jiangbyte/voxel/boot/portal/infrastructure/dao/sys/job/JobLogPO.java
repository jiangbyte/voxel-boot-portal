package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job;

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
 * 任务执行日志 PO，对应表 sys_job_log。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "sys_job_log", autoResultMap = true)
public class JobLogPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String jobId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;

    private LocalDateTime startedAt;
    private Long durationMs;
    private Integer success;
    private String result;
    private String executor;
    private String ip;
    private String processId;
    private String appDir;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

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
 * 任务定义 PO，对应表 sys_job。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "sys_job", autoResultMap = true)
public class JobPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String name;
    private String handler;
    private String triggerType;
    private String triggerConfig;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;

    private LocalDateTime lastRunTime;
    private LocalDateTime nextRunTime;
    private String lastResult;
    private Integer enabled;
    private String description;
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

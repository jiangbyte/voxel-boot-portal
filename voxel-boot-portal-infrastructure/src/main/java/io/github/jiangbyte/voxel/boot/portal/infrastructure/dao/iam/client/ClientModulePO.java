package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client;

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
 * 客户端模块持久化对象，对应表 sys_client_module。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "sys_client_module", autoResultMap = true)
public class ClientModulePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String name;
    private String code;
    private String accountType;
    private String icon;
    private String color;
    private Integer sort;
    private String status;
    private String description;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extra;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

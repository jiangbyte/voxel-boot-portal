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
 * 客户端资源持久化对象，对应表 sys_client_resource。
 *
 * Author: Charlie
 */
@Data
@TableName(value = "sys_client_resource", autoResultMap = true)
public class ClientResourcePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String parentId;
    private String code;
    private String name;
    private String resourceType;
    private String moduleId;
    private String path;
    private String component;
    private String redirect;
    private String icon;
    private String color;
    private String href;
    private Integer sort;
    private Integer isVisible;
    private Integer isCache;
    private Integer isAffix;
    private String status;
    private String description;
    private String layout;
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

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置持久化对象，对应表 sys_config。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_config")
public class ConfigPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String configKey;

    /** 标量配置值（STRING/NUMBER/密文）。 */
    private String configValue;

    /** 复杂配置（JSON：list/object）。 */
    private String configJson;

    private String category;

    private String remark;

    private Integer sortCode;

    private String valueType;

    private String label;

    private String scope;

    private String scene;

    private Integer isBuiltin;

    private String extJson;

    /** 乐观锁版本（仓储层手动 WHERE version=?）。 */
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

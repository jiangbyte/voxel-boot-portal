package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色持久化对象，对应表 sys_role。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_role")
public class RolePO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String code;

    private String name;

    private String category;

    private String scopeType;

    private String ownerDeptId;

    private Integer sort;

    private String status;

    private Integer isBuiltin;

    private String description;

    /** JSON 扩展字段，库表 NOT NULL，缺省空对象。 */
    private String extra;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

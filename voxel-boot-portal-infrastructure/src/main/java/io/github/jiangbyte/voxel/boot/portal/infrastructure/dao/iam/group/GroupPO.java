package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.group;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户组持久化对象，对应表 sys_group。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_group")
public class GroupPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String ownerDeptId;

    private String description;

    private String status;

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

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.dept;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门持久化对象，对应表 sys_dept。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_dept")
public class DeptPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String parentId;

    private String masterId;

    private String deputyMasterId;

    private String name;

    private String category;

    private Integer sort;

    private Integer isVirtual;

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

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 弱密码持久化对象，对应表 sys_weak_password。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_weak_password")
public class WeakPasswordPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String password;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

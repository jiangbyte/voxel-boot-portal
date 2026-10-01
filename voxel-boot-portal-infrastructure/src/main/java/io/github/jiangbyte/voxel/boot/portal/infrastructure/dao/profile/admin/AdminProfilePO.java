package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.admin;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端资料持久化对象，对应表 profile_user_admin。
 *
 * Author: Charlie
 */
@Data
@TableName("profile_user_admin")
public class AdminProfilePO {

    @TableId(value = "account_id", type = IdType.INPUT)
    private String accountId;

    private String nickname;

    private String avatar;

    private String signature;

    private String phone;

    private String email;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

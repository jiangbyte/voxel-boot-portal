package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统账号持久化对象，对应表 sys_account。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_account")
public class AccountPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String passwordHash;

    private String accountType;

    private String accountStatus;

    private LocalDateTime cancelledAt;

    private String cancelledBy;

    private String cancelReason;

    private String cancelNotifyEmail;

    private String cancelNotifyPhone;

    private String lastLoginIp;

    private String lastLoginAddress;

    private LocalDateTime lastLoginTime;

    private String lastLoginDevice;

    private String latestLoginIp;

    private String latestLoginAddress;

    private LocalDateTime latestLoginTime;

    private String latestLoginDevice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

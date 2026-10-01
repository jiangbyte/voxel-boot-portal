package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账号身份持久化对象，对应表 sys_account_identity。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_account_identity")
public class AccountIdentityPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String accountId;

    private String identityType;

    private String identifier;

    private Integer verified;

    private Integer isPrimary;

    private String bindStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

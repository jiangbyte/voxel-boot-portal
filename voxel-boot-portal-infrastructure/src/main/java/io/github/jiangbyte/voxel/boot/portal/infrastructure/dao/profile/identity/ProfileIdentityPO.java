package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实名快照 PO，对应表 profile_identity。
 *
 * Author: Charlie
 */
@Data
@TableName("profile_identity")
public class ProfileIdentityPO {

    @TableId(value = "account_id", type = IdType.INPUT)
    private String accountId;
    private String status;
    private String documentType;
    private String realNameCipher;
    private String documentNoCipher;
    private String documentNoHash;
    private String verifyChannel;
    private String provider;
    private String providerOrderNo;
    private LocalDateTime verifiedAt;
    private String sourceCaseId;
    private LocalDateTime revokedAt;
    private String revokedBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

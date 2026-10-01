package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.oauth;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * OAuth 绑定 PO，对应 sys_account_oauth_binding。
 *
 * Author: Charlie
 */
@Data
@TableName("sys_account_oauth_binding")
public class AccountOauthBindingPO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String accountId;
    private String provider;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private String rawProfile;
    private LocalDateTime boundAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

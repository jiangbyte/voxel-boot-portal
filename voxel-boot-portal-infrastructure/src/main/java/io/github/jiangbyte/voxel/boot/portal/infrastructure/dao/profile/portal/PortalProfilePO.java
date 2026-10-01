package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.portal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 门户资料 PO，对应表 profile_user_portal。
 *
 * Author: Charlie
 */
@Data
@TableName("profile_user_portal")
public class PortalProfilePO {

    @TableId(value = "account_id", type = IdType.INPUT)
    private String accountId;
    private String nickname;
    private String avatar;
    private String signature;
    private String phone;
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
}

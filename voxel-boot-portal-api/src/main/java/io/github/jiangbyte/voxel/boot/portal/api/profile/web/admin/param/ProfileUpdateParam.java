package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理端更新个人资料入参。
 *
 * Author: Charlie
 */
@Schema(description = "管理端更新个人资料入参")
@Data
public class ProfileUpdateParam {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "个性签名")
    private String signature;

    @Schema(description = "备注")
    private String remark;
}

package io.github.jiangbyte.voxel.boot.portal.api.auth.web.oauth.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "微信小程序登录参数")
@Data
public class WechatMpLoginParam {
    @NotBlank
    @Schema(description = "wx.login code")
    private String code;
}

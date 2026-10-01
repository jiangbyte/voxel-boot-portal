package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ResetPasswordParam。
 *
 * Author: Charlie
 */
@Schema(description = "ResetPasswordParam")
@Data
public class ResetPasswordParam {

    @Schema(description = "可选邮箱") private String email;
    @NotBlank @Schema(description = "token") private String token;
    @NotBlank @Schema(description = "password") private String password;
    @NotBlank @Schema(description = "passwordKeyId") private String passwordKeyId;
    @NotBlank @Schema(description = "captchaId") private String captchaId;
    @NotBlank @Schema(description = "captchaValue") private String captchaValue;

}

package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ResetPasswordByPhoneParam。
 *
 * Author: Charlie
 */
@Schema(description = "ResetPasswordByPhoneParam")
@Data
public class ResetPasswordByPhoneParam {

    @NotBlank @Schema(description = "phone") private String phone;
    @NotBlank @Schema(description = "otpCode") private String otpCode;
    @NotBlank @Schema(description = "password") private String password;
    @NotBlank @Schema(description = "passwordKeyId") private String passwordKeyId;
    @NotBlank @Schema(description = "captchaId") private String captchaId;
    @NotBlank @Schema(description = "captchaValue") private String captchaValue;

}

package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RegisterParam。
 *
 * Author: Charlie
 */
@Schema(description = "RegisterParam")
@Data
public class RegisterParam {

    @NotBlank @Schema(description = "ACCOUNT | EMAIL | PHONE") private String registerChannel;
    @Schema(description = "账号") private String account;
    @Schema(description = "email") private String email;
    @Schema(description = "phone") private String phone;
    @Schema(description = "otpCode") private String otpCode;
    @NotBlank @Schema(description = "password") private String password;
    @NotBlank @Schema(description = "passwordKeyId") private String passwordKeyId;
    @NotBlank @Schema(description = "captchaId") private String captchaId;
    @NotBlank @Schema(description = "captchaValue") private String captchaValue;

}

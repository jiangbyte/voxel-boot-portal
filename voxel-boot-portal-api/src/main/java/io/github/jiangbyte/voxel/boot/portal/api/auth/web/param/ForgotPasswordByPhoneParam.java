package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ForgotPasswordByPhoneParam。
 *
 * Author: Charlie
 */
@Schema(description = "ForgotPasswordByPhoneParam")
@Data
public class ForgotPasswordByPhoneParam {

    @NotBlank @Schema(description = "phone") private String phone;
    @NotBlank @Schema(description = "captchaId") private String captchaId;
    @NotBlank @Schema(description = "captchaValue") private String captchaValue;

}

package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ForgotPasswordParam。
 *
 * Author: Charlie
 */
@Schema(description = "ForgotPasswordParam")
@Data
public class ForgotPasswordParam {

    @NotBlank @Schema(description = "email") private String email;
    @NotBlank @Schema(description = "captchaId") private String captchaId;
    @NotBlank @Schema(description = "captchaValue") private String captchaValue;

}

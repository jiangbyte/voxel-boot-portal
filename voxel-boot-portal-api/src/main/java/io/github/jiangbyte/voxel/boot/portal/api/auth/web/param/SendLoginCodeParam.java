package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * SendLoginCodeParam。
 *
 * Author: Charlie
 */
@Schema(description = "SendLoginCodeParam")
@Data
public class SendLoginCodeParam {

    @NotBlank @Schema(description = "邮箱或手机") private String target;
    @NotBlank @Schema(description = "EMAIL 或 PHONE") private String channel;
    @NotBlank @Schema(description = "captchaId") private String captchaId;
    @NotBlank @Schema(description = "captchaValue") private String captchaValue;

}

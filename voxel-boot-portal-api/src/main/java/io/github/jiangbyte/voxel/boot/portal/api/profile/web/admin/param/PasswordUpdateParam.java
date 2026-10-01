package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码请求。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "修改密码请求")
public class PasswordUpdateParam {
    @NotBlank
    @Schema(description = "passwordKeyId")
    private String passwordKeyId;
    @Schema(description = "旧密码（OLD_PASSWORD 模式）")
    private String oldPassword;
    @NotBlank
    @Schema(description = "newPassword")
    private String newPassword;
    @Schema(description = "OTP（EMAIL_CODE/PHONE_CODE 模式）")
    private String otpCode;
}

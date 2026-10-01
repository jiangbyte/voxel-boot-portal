package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新邮箱请求。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "更新邮箱请求")
public class EmailUpdateParam {
    @NotBlank
    @Schema(description = "passwordKeyId")
    private String passwordKeyId;
    @NotBlank
    @Schema(description = "当前登录密码密文")
    private String password;
    @Schema(description = "email")
    private String email;
    @Schema(description = "是否允许邮箱登录")
    private Integer emailLoginEnabled;
    @Schema(description = "otpCode")
    private String otpCode;
}

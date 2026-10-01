package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新手机号请求。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "更新手机号请求")
public class PhoneUpdateParam {
    @NotBlank
    @Schema(description = "passwordKeyId")
    private String passwordKeyId;
    @NotBlank
    @Schema(description = "当前登录密码密文")
    private String password;
    @Schema(description = "phone")
    private String phone;
    @Schema(description = "是否允许手机登录")
    private Integer phoneLoginEnabled;
    @Schema(description = "otpCode")
    private String otpCode;
}

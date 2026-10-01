package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求：支持账号/邮箱/手机 + 密码或 OTP，可选图形验证码与 RSA 密文密码。
 *
 * Author: Charlie
 */
@Schema(description = "登录请求参数")
@Data
public class LoginParam {

    @NotBlank
    @Schema(description = "登录账号/邮箱/手机")
    private String account;

    @Schema(description = "loginMode 为 PASSWORD 时必填")
    private String password;

    @Schema(description = "RSA 密钥 ID；缺省则 password 按明文处理")
    private String passwordKeyId;

    @Schema(description = "图形验证码 ID")
    private String captchaId;

    @Schema(description = "图形验证码值")
    private String captchaValue;

    @Schema(description = "身份类型 ACCOUNT/EMAIL/PHONE")
    private String identityType = "ACCOUNT";

    @Schema(description = "PASSWORD 或 OTP")
    private String loginMode = "PASSWORD";

    @Schema(description = "OTP 验证码")
    private String otpCode;

    @Schema(description = "记住我")
    private Integer rememberMe = 1;
}

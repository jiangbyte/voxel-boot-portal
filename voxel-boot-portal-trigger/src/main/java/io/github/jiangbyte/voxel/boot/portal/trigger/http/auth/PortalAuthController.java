package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth;

import io.github.jiangbyte.voxel.boot.portal.cases.auth.IAuthCase;
import io.github.jiangbyte.voxel.boot.portal.cases.auth.AuthCaseImpl;
import io.github.jiangbyte.voxel.boot.portal.cases.auth.AuthOptionsResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.CancelAccountParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.ForgotPasswordByPhoneParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.ForgotPasswordParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.LoginParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.RegisterParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.ResetPasswordByPhoneParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.ResetPasswordParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.param.SendLoginCodeParam;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result.CaptchaHttpResult;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result.LoginHttpResult;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result.PasswordKeyHttpResult;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result.RegisterHttpResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

/**
 * 门户端认证 API：登录选项、注册、登录/登出、刷新、找回密码与注销。
 *
 * Author: Charlie
 */
@Tag(name = "门户端认证 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalAuthController {

    private final IAuthCase authApplicationService;


    @Operation(summary = "获取门户登录页公开配置")
    @GetMapping("/v1/portal/auth/public/auth-options")
    public ApiResponse<AuthOptionsResult> authOptions() {
        return ApiResponse.ok(authApplicationService.authOptions(AccountType.PORTAL));
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/v1/portal/auth/session/captcha")
    public ApiResponse<CaptchaHttpResult> captcha() {
        return ApiResponse.ok(CaptchaHttpResult.from(authApplicationService.captcha()));
    }

    @Operation(summary = "获取密码传输 RSA 公钥")
    @GetMapping("/v1/portal/auth/session/password-key")
    public ApiResponse<PasswordKeyHttpResult> passwordKey() {
        return ApiResponse.ok(PasswordKeyHttpResult.from(authApplicationService.passwordKey()));
    }

    @Operation(summary = "发送门户登录 OTP")
    @PostMapping("/v1/portal/auth/session/send-login-code")
    public ApiResponse<Void> sendLoginCode(@Valid @RequestBody SendLoginCodeParam request) {
        authApplicationService.sendLoginCode(toSend(request), AccountType.PORTAL);
        return ApiResponse.ok();
    }

    @Operation(summary = "门户登录")
    @PostMapping("/v1/portal/auth/session/login")
    public ApiResponse<LoginHttpResult> login(@Valid @RequestBody LoginParam request) {
        return ApiResponse.ok(LoginHttpResult.from(authApplicationService.portalLogin(toLogin(request))));
    }

    @Operation(summary = "刷新门户会话 Token")
    @PostMapping("/v1/portal/auth/session/refresh")
    public ApiResponse<LoginHttpResult> refresh() {
        return ApiResponse.ok(LoginHttpResult.from(authApplicationService.refreshSession()));
    }

    @Operation(summary = "门户用户注册")
    @PostMapping("/v1/portal/auth/session/register")
    public ApiResponse<RegisterHttpResult> register(@Valid @RequestBody RegisterParam request) {
        AuthCaseImpl.RegisterCommand command = new AuthCaseImpl.RegisterCommand();
        command.setRegisterChannel(request.getRegisterChannel());
        command.setAccount(request.getAccount());
        command.setEmail(request.getEmail());
        command.setPhone(request.getPhone());
        command.setOtpCode(request.getOtpCode());
        command.setPassword(request.getPassword());
        command.setPasswordKeyId(request.getPasswordKeyId());
        command.setCaptchaId(request.getCaptchaId());
        command.setCaptchaValue(request.getCaptchaValue());
        return ApiResponse.ok(RegisterHttpResult.from(authApplicationService.registerPortal(command)));
    }

    @Operation(summary = "发送门户注册 OTP")
    @PostMapping("/v1/portal/auth/session/register/send-code")
    public ApiResponse<Void> sendRegisterCode(@Valid @RequestBody SendLoginCodeParam request) {
        authApplicationService.sendRegisterCode(toSend(request));
        return ApiResponse.ok();
    }

    @Operation(summary = "门户登出")
    @PostMapping("/v1/portal/auth/session/logout")
    public ApiResponse<Void> logout() {
        authApplicationService.logoutPortal();
        return ApiResponse.ok();
    }

    @Operation(summary = "门户忘记密码（发重置邮件）")
    @PostMapping("/v1/portal/auth/session/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordParam request) {
        authApplicationService.forgotPassword(
                request.getEmail(), request.getCaptchaId(), request.getCaptchaValue(), AccountType.PORTAL);
        return ApiResponse.ok();
    }

    @Operation(summary = "门户忘记密码（手机 OTP）")
    @PostMapping("/v1/portal/auth/session/forgot-password/phone")
    public ApiResponse<Void> forgotPasswordByPhone(@Valid @RequestBody ForgotPasswordByPhoneParam request) {
        authApplicationService.forgotPasswordByPhone(
                request.getPhone(), request.getCaptchaId(), request.getCaptchaValue(), AccountType.PORTAL);
        return ApiResponse.ok();
    }

    @Operation(summary = "门户通过令牌重置密码")
    @PostMapping("/v1/portal/auth/session/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordParam request) {
        authApplicationService.resetPassword(
                request.getToken(), request.getPasswordKeyId(), request.getPassword(),
                request.getCaptchaId(), request.getCaptchaValue(), AccountType.PORTAL);
        return ApiResponse.ok();
    }

    @Operation(summary = "门户通过手机 OTP 重置密码")
    @PostMapping("/v1/portal/auth/session/reset-password/phone")
    public ApiResponse<Void> resetPasswordByPhone(@Valid @RequestBody ResetPasswordByPhoneParam request) {
        authApplicationService.resetPasswordByPhone(
                request.getPhone(), request.getOtpCode(), request.getPasswordKeyId(), request.getPassword(),
                request.getCaptchaId(), request.getCaptchaValue(), AccountType.PORTAL);
        return ApiResponse.ok();
    }

    @Operation(summary = "注销当前门户账号")
    @PostMapping("/v1/portal/auth/session/cancel")
    public ApiResponse<Void> cancel(@RequestBody(required = false) CancelAccountParam request) {
        String reason = request == null ? null : request.getCancelReason();
        authApplicationService.cancelPortal(reason);
        return ApiResponse.ok();
    }

    private static AuthCaseImpl.LoginCommand toLogin(LoginParam request) {
        AuthCaseImpl.LoginCommand command = new AuthCaseImpl.LoginCommand();
        command.setAccount(request.getAccount());
        command.setPassword(request.getPassword());
        command.setPasswordKeyId(request.getPasswordKeyId());
        command.setCaptchaId(request.getCaptchaId());
        command.setCaptchaValue(request.getCaptchaValue());
        command.setIdentityType(request.getIdentityType());
        command.setLoginMode(request.getLoginMode());
        command.setOtpCode(request.getOtpCode());
        command.setRememberMe(request.getRememberMe());
        return command;
    }

    private static AuthCaseImpl.SendCodeCommand toSend(SendLoginCodeParam request) {
        AuthCaseImpl.SendCodeCommand command = new AuthCaseImpl.SendCodeCommand();
        command.setTarget(request.getTarget());
        command.setChannel(request.getChannel());
        command.setCaptchaId(request.getCaptchaId());
        command.setCaptchaValue(request.getCaptchaValue());
        return command;
    }
}

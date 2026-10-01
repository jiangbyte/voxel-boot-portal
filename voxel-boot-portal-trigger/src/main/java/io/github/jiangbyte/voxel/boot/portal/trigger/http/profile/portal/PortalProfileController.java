package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler.PublicProfileResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler.MeResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler.AvatarUpdateResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.cases.auth.IAuthCredentialCase;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.IPortalProfileCase;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.UpdatePortalProfileCommand;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.param.BindCodeParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.param.EmailUpdateParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.param.PasswordUpdateParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.param.PhoneUpdateParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.param.ProfileUpdateParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.AvatarUpdateResult;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.MeResult;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.PublicProfileResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

/**
 * 门户端用户中心 API：当前用户、资料/头像、改密换绑与公开空间资料。
 *
 * Author: Charlie
 */
@Tag(name = "门户端用户中心 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalProfileController {

    private final IPortalProfileCase portalProfileApplicationService;
    private final IAuthCredentialCase authCredentialApplicationService;


    /** 获取当前登录门户用户摘要。 */
    @Operation(summary = "获取当前登录门户用户摘要")
    @GetMapping("/v1/portal/auth/session/me")
    public ApiResponse<MeResult> me() {
        return ApiResponse.ok(MeResultAssembler.INSTANCE.toResult(portalProfileApplicationService.currentMe()));
    }

    /** 更新当前用户个人资料。 */
    @Operation(summary = "更新当前用户个人资料")
    @PostMapping("/v1/portal/profile/account/update")
    public ApiResponse<Void> updateProfile(@Valid @RequestBody ProfileUpdateParam request) {
        portalProfileApplicationService.updateCurrentProfile(new UpdatePortalProfileCommand(
                request.getNickname(), request.getAvatar(), request.getSignature()));
        return ApiResponse.ok();
    }

    /** 上传并替换当前用户头像。 */
    @Operation(summary = "上传并替换当前用户头像")
    @PostMapping("/v1/portal/profile/account/avatar/upload")
    public ApiResponse<AvatarUpdateResult> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException("File is required");
        }
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        try {
            return ApiResponse.ok(AvatarUpdateResultAssembler.INSTANCE.toResult(
                    portalProfileApplicationService.uploadAvatar(filename, contentType, size, file.getInputStream())));
        } catch (java.io.IOException ex) {
            throw new io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException(500, "Failed to read upload stream");
        }
    }

    /** 发送改密验证码。 */
    @Operation(summary = "发送改密验证码")
    @PostMapping("/v1/portal/profile/account/password/send-code")
    public ApiResponse<Void> sendPasswordCode() {
        authCredentialApplicationService.sendChangePasswordCode(AccountType.PORTAL);
        return ApiResponse.ok();
    }

    /** 修改当前用户登录密码。 */
    @Operation(summary = "修改当前用户登录密码")
    @PostMapping("/v1/portal/profile/account/password/update")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody PasswordUpdateParam request) {
        authCredentialApplicationService.updateCurrentPassword(
                AccountType.PORTAL,
                request.getPasswordKeyId(),
                request.getOldPassword(),
                request.getNewPassword(),
                request.getOtpCode());
        return ApiResponse.ok();
    }

    /** 向待绑定手机发送验证码。 */
    @Operation(summary = "向待绑定手机发送验证码")
    @PostMapping("/v1/portal/profile/account/phone/send-code")
    public ApiResponse<Void> sendPhoneCode(@Valid @RequestBody BindCodeParam request) {
        authCredentialApplicationService.sendBindPhoneCode(AccountType.PORTAL, request.getTarget());
        return ApiResponse.ok();
    }

    /** 更新当前用户手机号。 */
    @Operation(summary = "更新当前用户手机号")
    @PostMapping("/v1/portal/profile/account/phone/update")
    public ApiResponse<Void> updatePhone(@Valid @RequestBody PhoneUpdateParam request) {
        authCredentialApplicationService.updateCurrentPhone(
                AccountType.PORTAL,
                request.getPasswordKeyId(),
                request.getPassword(),
                request.getPhone(),
                request.getPhoneLoginEnabled(),
                request.getOtpCode());
        return ApiResponse.ok();
    }

    /** 向待绑定邮箱发送验证码。 */
    @Operation(summary = "向待绑定邮箱发送验证码")
    @PostMapping("/v1/portal/profile/account/email/send-code")
    public ApiResponse<Void> sendEmailCode(@Valid @RequestBody BindCodeParam request) {
        authCredentialApplicationService.sendBindEmailCode(AccountType.PORTAL, request.getTarget());
        return ApiResponse.ok();
    }

    /** 更新当前用户邮箱。 */
    @Operation(summary = "更新当前用户邮箱")
    @PostMapping("/v1/portal/profile/account/email/update")
    public ApiResponse<Void> updateEmail(@Valid @RequestBody EmailUpdateParam request) {
        authCredentialApplicationService.updateCurrentEmail(
                AccountType.PORTAL,
                request.getPasswordKeyId(),
                request.getPassword(),
                request.getEmail(),
                request.getEmailLoginEnabled(),
                request.getOtpCode());
        return ApiResponse.ok();
    }

    /** 按账号 ID 查询门户公开资料。 */
    @Operation(summary = "按账号 ID 查询门户公开资料")
    @GetMapping("/v1/portal/sys/space/detail")
    public ApiResponse<PublicProfileResult> spacesDetail(@RequestParam("account_id") String accountId) {
        return ApiResponse.ok(PublicProfileResultAssembler.INSTANCE.toResult(portalProfileApplicationService.publicProfile(accountId)));
    }
}

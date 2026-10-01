package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.oauth;

import io.github.jiangbyte.voxel.boot.portal.cases.auth.oauth.IOauthCase;
import io.github.jiangbyte.voxel.boot.portal.cases.auth.oauth.OauthCaseImpl;
import io.github.jiangbyte.voxel.boot.portal.cases.auth.oauth.OauthAuthorizeResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.oauth.param.OauthExchangeParam;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.oauth.param.WechatMpLoginParam;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result.LoginHttpResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 门户端三方登录 API。
 *
 * Author: Charlie
 */
@Tag(name = "门户端三方登录 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalOauthController {

    private final IOauthCase oauthApplicationService;


    @Operation(summary = "OAuth 授权")
    @GetMapping("/v1/portal/auth/oauth/{provider}/authorize")
    public ApiResponse<OauthAuthorizeResult> authorize(
            @PathVariable("provider") String provider,
            @RequestParam(value = "intent", required = false, defaultValue = "LOGIN") String intent,
            @RequestParam(value = "redirect", required = false) String redirect) {
        return ApiResponse.ok(oauthApplicationService.authorize(AccountType.PORTAL, provider, intent, redirect));
    }

    @Operation(summary = "OAuth 回调")
    @GetMapping("/v1/portal/auth/oauth/{provider}/callback")
    public void callback(
            @PathVariable("provider") String provider,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws IOException {
        response.sendRedirect(oauthApplicationService.handleCallback(AccountType.PORTAL, provider, code, state));
    }

    @Operation(summary = "OAuth 换票")
    @PostMapping("/v1/portal/auth/oauth/exchange")
    public ApiResponse<LoginHttpResult> exchange(@Valid @RequestBody OauthExchangeParam request) {
        return ApiResponse.ok(LoginHttpResult.from(oauthApplicationService.exchange(request.getCode())));
    }

    @Operation(summary = "微信小程序登录")
    @PostMapping("/v1/portal/auth/oauth/wechat-mp/login")
    public ApiResponse<LoginHttpResult> wechatMpLogin(@Valid @RequestBody WechatMpLoginParam request) {
        return ApiResponse.ok(LoginHttpResult.from(
                oauthApplicationService.loginWechatMp(AccountType.PORTAL, request.getCode())));
    }

    @Operation(summary = "当前账号 OAuth 绑定列表")
    @GetMapping("/v1/portal/auth/oauth/bindings")
    public ApiResponse<List<OauthCaseImpl.OauthBindingResult>> bindings() {
        return ApiResponse.ok(oauthApplicationService.listCurrentBindings());
    }

    @Operation(summary = "发起 OAuth 绑定授权")
    @PostMapping("/v1/portal/auth/oauth/{provider}/bind/authorize")
    public ApiResponse<OauthAuthorizeResult> bindAuthorize(@PathVariable("provider") String provider) {
        return ApiResponse.ok(oauthApplicationService.bindAuthorize(AccountType.PORTAL, provider));
    }

    @Operation(summary = "解绑当前账号 OAuth")
    @PostMapping("/v1/portal/auth/oauth/{provider}/unbind")
    public ApiResponse<Void> unbind(@PathVariable("provider") String provider) {
        oauthApplicationService.unbind(provider);
        return ApiResponse.ok();
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.auth.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthClientPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthUserProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWeChatOpenRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * JustAuth / 微信小程序客户端实现。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class OauthClientPortImpl implements OauthClientPort {

    private final ConfigApi configApi;
    private final ObjectMapper objectMapper;


    @Override
    public void ensureEnabled(String accountType, String provider) {
        OauthProvider oauthProvider = OauthProvider.from(provider);
        if (!isEnabled(accountType, provider)) {
            throw new DomainException(oauthProvider.getLabel() + " 登录未启用");
        }
    }

    @Override
    public boolean isEnabled(String accountType, String provider) {
        return configBool(configKey(accountType, OauthProvider.from(provider), "ENABLED"), false);
    }

    @Override
    public String providerLabel(String provider) {
        try {
            return OauthProvider.from(provider).getLabel();
        } catch (Exception ex) {
            return provider;
        }
    }

    @Override
    public boolean isWebOAuth(String provider) {
        return OauthProvider.from(provider).isWebOAuth();
    }

    @Override
    public String buildAuthorizeUrl(String accountType, String provider, String state) {
        // 1. 校验网页 OAuth
        OauthProvider oauthProvider = OauthProvider.from(provider);
        if (!oauthProvider.isWebOAuth()) {
            throw new DomainException("该提供商不支持网页授权");
        }
        // 2. 构建 JustAuth 请求并生成授权 URL
        return createAuthRequest(accountType, oauthProvider).authorize(state);
    }

    @Override
    public OauthUserProfile loginByCode(String accountType, String provider, String code, String state) {
        OauthProvider oauthProvider = OauthProvider.from(provider);
        if (!oauthProvider.isWebOAuth()) {
            throw new DomainException("该提供商不支持网页授权回调");
        }
        AuthRequest request = createAuthRequest(accountType, oauthProvider);
        AuthCallback callback = AuthCallback.builder().code(code).state(state).build();
        AuthResponse<?> response;
        try {
            response = request.login(callback);
        } catch (AuthException ex) {
            throw new DomainException("三方登录失败: " + ex.getMessage());
        }
        if (response == null || !response.ok() || !(response.getData() instanceof AuthUser user)) {
            String msg = response == null ? "无响应" : String.valueOf(response.getMsg());
            throw new DomainException("三方登录失败: " + msg);
        }
        return toProfile(oauthProvider, user);
    }

    @Override
    public OauthUserProfile loginWechatMp(String accountType, String jsCode) {
        // 1. 校验启用与配置
        ensureEnabled(accountType, OauthProvider.WECHAT_MP.name());
        String appId = configValue(accountType, OauthProvider.WECHAT_MP, "APP_ID");
        String secret = configValue(accountType, OauthProvider.WECHAT_MP, "APP_SECRET");
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(secret)) {
            throw new DomainException("微信小程序未配置 AppId/AppSecret");
        }
        if (!StringUtils.hasText(jsCode)) {
            throw new DomainException("缺少 code");
        }
        // 2. 调用 jscode2session
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + enc(appId)
                + "&secret=" + enc(secret)
                + "&js_code=" + enc(jsCode.trim())
                + "&grant_type=authorization_code";
        String body = RestClient.create().get().uri(url).retrieve().body(String.class);
        try {
            JsonNode json = objectMapper.readTree(body == null ? "{}" : body);
            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                throw new DomainException("微信小程序登录失败: " + json.path("errmsg").asText());
            }
            String openId = json.path("openid").asText(null);
            if (!StringUtils.hasText(openId)) {
                throw new DomainException("微信小程序登录失败: 未返回 openid");
            }
            OauthUserProfile profile = new OauthUserProfile();
            profile.setProvider(OauthProvider.WECHAT_MP.name());
            profile.setOpenId(openId);
            String unionId = json.path("unionid").asText(null);
            profile.setUnionId(StringUtils.hasText(unionId) ? unionId : null);
            profile.setRawProfileJson(body);
            return profile;
        } catch (DomainException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DomainException("微信小程序登录失败: 响应解析错误");
        }
    }

    private AuthRequest createAuthRequest(String accountType, OauthProvider provider) {
        ensureEnabled(accountType, provider.name());
        String clientId = firstNonBlank(
                configValue(accountType, provider, "CLIENT_ID"),
                configValue(accountType, provider, "APP_ID"));
        String clientSecret = firstNonBlank(
                configValue(accountType, provider, "CLIENT_SECRET"),
                configValue(accountType, provider, "APP_SECRET"));
        String redirectUri = configValue(accountType, provider, "REDIRECT_URI");
        if (!StringUtils.hasText(redirectUri)) {
            throw new DomainException("请配置 " + configKey(accountType, provider, "REDIRECT_URI"));
        }
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw new DomainException(provider.getLabel() + " 未配置 ClientId/Secret");
        }
        AuthConfig config = AuthConfig.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri.trim())
                .ignoreCheckState(true)
                .build();
        return switch (provider) {
            case GITHUB -> new AuthGithubRequest(config);
            case GITEE -> new AuthGiteeRequest(config);
            case QQ -> new AuthQqRequest(config);
            case WECHAT_OPEN -> new AuthWeChatOpenRequest(config);
            case WECHAT_MP -> throw new DomainException("小程序请使用 code 登录接口");
        };
    }

    private OauthUserProfile toProfile(OauthProvider provider, AuthUser user) {
        OauthUserProfile profile = new OauthUserProfile();
        profile.setProvider(provider.name());
        profile.setOpenId(user.getUuid());
        profile.setUnionId(extractUnionId(user));
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        ObjectNode raw = objectMapper.createObjectNode();
        raw.put("uuid", user.getUuid());
        raw.put("username", user.getUsername());
        raw.put("nickname", user.getNickname());
        raw.put("avatar", user.getAvatar());
        raw.put("email", user.getEmail());
        raw.put("source", user.getSource());
        if (user.getRawUserInfo() != null) {
            raw.set("rawUserInfo", objectMapper.valueToTree(user.getRawUserInfo()));
        }
        try {
            profile.setRawProfileJson(objectMapper.writeValueAsString(raw));
        } catch (Exception e) {
            profile.setRawProfileJson("{}");
        }
        return profile;
    }

    @SuppressWarnings("unchecked")
    private static String extractUnionId(AuthUser user) {
        Object raw = user.getRawUserInfo();
        if (raw instanceof Map<?, ?> map) {
            Object unionId = map.get("unionid");
            if (unionId == null) {
                unionId = map.get("unionId");
            }
            return unionId == null ? null : String.valueOf(unionId);
        }
        return null;
    }

    private String configValue(String accountType, OauthProvider provider, String field) {
        return configApi.getByKey(configKey(accountType, provider, field), "").trim();
    }

    private boolean configBool(String key, boolean defaultValue) {
        String value = configApi.getByKey(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        String v = value.trim().toLowerCase(Locale.ROOT);
        return "true".equals(v) || "1".equals(v) || "yes".equals(v);
    }

    public static String configKey(String accountType, OauthProvider provider, String field) {
        String type = accountType == null ? "PORTAL" : accountType.trim().toUpperCase(Locale.ROOT);
        return "AUTH_OAUTH_" + type + "_" + provider.name() + "_" + field;
    }

    private static String firstNonBlank(String a, String b) {
        if (StringUtils.hasText(a)) {
            return a.trim();
        }
        return StringUtils.hasText(b) ? b.trim() : "";
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

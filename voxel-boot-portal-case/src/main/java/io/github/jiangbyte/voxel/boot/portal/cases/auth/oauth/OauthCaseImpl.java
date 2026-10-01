package io.github.jiangbyte.voxel.boot.portal.cases.auth.oauth;

import io.github.jiangbyte.voxel.boot.portal.cases.auth.IAuthCase;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.AccountOauthPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthBindingInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthClientPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthExchangePort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthStatePayload;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthStatePort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthUserProfile;
import lombok.RequiredArgsConstructor;

/**
 * OAuth 用例：授权、回调、换票、绑定/解绑、小程序登录。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class OauthCaseImpl implements IOauthCase {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> WECHAT_FAMILY = Set.of("WECHAT_OPEN", "WECHAT_MP");

    private final OauthClientPort oauthClientPort;
    private final OauthStatePort oauthStatePort;
    private final OauthExchangePort oauthExchangePort;
    private final AccountOauthPort accountOauthPort;
    private final AccountApi accountApi;
    private final IAuthCase authApplicationService;
    private final SessionPort sessionPort;
    private final ConfigApi configApi;

    @Value("${voxel.app.frontend-base-url:http://localhost:5173}")
    private String adminFrontendBaseUrl;

    @Value("${voxel.app.portal-frontend-base-url:http://localhost:5174}")
    private String portalFrontendBaseUrl;


    /** 发起授权。 */
    public OauthAuthorizeResult authorize(AccountType accountType, String provider, String intent, String redirect) {
        // 1. 校验提供商与启用状态
        AccountType type = accountType == null ? AccountType.PORTAL : accountType;
        if (!oauthClientPort.isWebOAuth(provider)) {
            throw new DomainException("请使用小程序登录接口");
        }
        oauthClientPort.ensureEnabled(type.name(), provider);
        String normalizedIntent = StringUtils.hasText(intent) ? intent.trim().toUpperCase(Locale.ROOT) : "LOGIN";
        if (!"LOGIN".equals(normalizedIntent) && !"BIND".equals(normalizedIntent)) {
            throw new DomainException("不支持的 OAuth intent");
        }
        // 2. BIND 需已登录
        OauthStatePayload payload = new OauthStatePayload();
        payload.setAccountType(type.name());
        payload.setIntent(normalizedIntent);
        payload.setProvider(provider.trim().toUpperCase(Locale.ROOT));
        payload.setRedirect(redirect);
        if ("BIND".equals(normalizedIntent)) {
            String accountId = sessionPort.currentAccountId();
            String currentType = sessionPort.currentAccountType();
            if (!StringUtils.hasText(accountId)) {
                throw new DomainException(401, "未登录");
            }
            if (!type.name().equalsIgnoreCase(currentType)) {
                throw new DomainException("账号类型不匹配");
            }
            payload.setAccountId(accountId);
        }
        // 3. 存 state 并返回授权 URL
        String state = oauthStatePort.save(payload);
        String authorizeUrl = oauthClientPort.buildAuthorizeUrl(type.name(), provider, state);
        OauthAuthorizeResult result = new OauthAuthorizeResult();
        result.setAuthorizeUrl(authorizeUrl);
        result.setState(state);
        return result;
    }

    /** 处理回调，返回前端重定向 URL。 */
    @Transactional
    public String handleCallback(AccountType accountType, String provider, String code, String state) {
        AccountType type = accountType == null ? AccountType.PORTAL : accountType;
        OauthStatePayload payload = oauthStatePort.consume(state);
        String frontend = frontendCallback(type);
        if (payload == null) {
            return failRedirect(frontend, "授权已过期，请重试");
        }
        if (!type.name().equalsIgnoreCase(payload.getAccountType())
                || !provider.trim().equalsIgnoreCase(payload.getProvider())) {
            return failRedirect(frontend, "授权状态不匹配");
        }
        try {
            OauthUserProfile profile = oauthClientPort.loginByCode(type.name(), provider, code, state);
            if ("BIND".equalsIgnoreCase(payload.getIntent())) {
                bindProfile(payload.getAccountId(), profile);
                return successRedirect(frontend, null, payload.getRedirect(), "bound");
            }
            LoginResult login = loginOrCreate(type, profile);
            return successRedirect(frontend, login, payload.getRedirect(), "login");
        } catch (DomainException ex) {
            return failRedirect(frontend, ex.getMessage());
        } catch (Exception ex) {
            return failRedirect(frontend, "三方登录失败");
        }
    }

    public LoginResult exchange(String code) {
        return oauthExchangePort.consume(code);
    }

    @Transactional
    public LoginResult loginWechatMp(AccountType accountType, String code) {
        AccountType type = accountType == null ? AccountType.PORTAL : accountType;
        if (type != AccountType.PORTAL) {
            throw new DomainException("管理端暂不支持小程序登录");
        }
        OauthUserProfile profile = oauthClientPort.loginWechatMp(type.name(), code);
        return loginOrCreate(type, profile);
    }

    public List<OauthBindingResult> listCurrentBindings() {
        String accountId = sessionPort.currentAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        return toResults(accountOauthPort.listByAccount(accountId));
    }

    public OauthAuthorizeResult bindAuthorize(AccountType accountType, String provider) {
        return authorize(accountType, provider, "BIND", null);
    }

    @Transactional
    public void unbind(String provider) {
        String accountId = sessionPort.currentAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        assertCanUnbind(accountId, provider);
        accountOauthPort.unbind(accountId, provider);
    }

    @Transactional
    public void adminUnbind(String accountId, String provider) {
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException("账号 ID 不能为空");
        }
        assertCanUnbind(accountId, provider);
        accountOauthPort.unbind(accountId, provider);
    }

    private LoginResult loginOrCreate(AccountType accountType, OauthUserProfile profile) {
        // 1. 已绑定则直接登录
        OauthBindingInfo binding = resolveBinding(profile);
        if (binding != null) {
            AccountInfo account = accountApi.getById(binding.getAccountId());
            if (account == null) {
                throw new DomainException("绑定账号不存在");
            }
            accountOauthPort.upsertBinding(
                    account.getId(), profile.getProvider(), profile.getOpenId(), profile.getUnionId(),
                    profile.getNickname(), profile.getAvatar(), profile.getRawProfileJson());
            String label = StringUtils.hasText(profile.getNickname())
                    ? profile.getNickname()
                    : accountApi.findIdentifier(account.getId(), "ACCOUNT");
            return authApplicationService.issueLoginForAccount(account, accountType, label);
        }
        // 2. 管理端必须先绑定
        if (accountType == AccountType.ADMIN) {
            throw new DomainException("请先使用账号密码登录后再绑定该三方账号");
        }
        // 3. 门户自动建号
        String accountName = allocateOauthAccountName(profile);
        byte[] random = new byte[32];
        RANDOM.nextBytes(random);
        String rawPassword = "oauth:" + Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        AccountInfo created = accountApi.createPortalAccount(
                accountName, null, accountApi.encodePassword(rawPassword));
        accountOauthPort.upsertBinding(
                created.getId(), profile.getProvider(), profile.getOpenId(), profile.getUnionId(),
                profile.getNickname(), profile.getAvatar(), profile.getRawProfileJson());
        return authApplicationService.issueLoginForAccount(created, AccountType.PORTAL, accountName);
    }

    private void bindProfile(String accountId, OauthUserProfile profile) {
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException("绑定失败：未登录");
        }
        OauthBindingInfo existing = resolveBinding(profile);
        if (existing != null && !accountId.equals(existing.getAccountId())) {
            throw new DomainException("该三方账号已绑定其他用户");
        }
        OauthBindingInfo sameProvider = accountOauthPort.listByAccount(accountId).stream()
                .filter(item -> profile.getProvider().equalsIgnoreCase(item.getProvider()))
                .findFirst().orElse(null);
        if (sameProvider != null && !sameProvider.getOpenId().equals(profile.getOpenId())) {
            throw new DomainException("已绑定其他 " + profile.getProvider() + " 账号，请先解绑");
        }
        accountOauthPort.upsertBinding(
                accountId, profile.getProvider(), profile.getOpenId(), profile.getUnionId(),
                profile.getNickname(), profile.getAvatar(), profile.getRawProfileJson());
    }

    private OauthBindingInfo resolveBinding(OauthUserProfile profile) {
        if (WECHAT_FAMILY.contains(profile.getProvider()) && StringUtils.hasText(profile.getUnionId())) {
            OauthBindingInfo byUnion = accountOauthPort.findByWechatUnionId(profile.getUnionId());
            if (byUnion != null) {
                return byUnion;
            }
        }
        return accountOauthPort.findByProviderOpenId(profile.getProvider(), profile.getOpenId());
    }

    private void assertCanUnbind(String accountId, String provider) {
        OauthBindingInfo target = accountOauthPort.listByAccount(accountId).stream()
                .filter(item -> provider.equalsIgnoreCase(item.getProvider()))
                .findFirst().orElse(null);
        if (target == null) {
            return;
        }
        int oauthCount = accountOauthPort.countByAccount(accountId);
        boolean hasAccount = StringUtils.hasText(accountApi.findIdentifier(accountId, "ACCOUNT"));
        boolean hasEmail = StringUtils.hasText(accountApi.findIdentifier(accountId, "EMAIL"));
        boolean hasPhone = StringUtils.hasText(accountApi.findIdentifier(accountId, "PHONE"));
        int otherLoginWays = (hasAccount ? 1 : 0) + (hasEmail ? 1 : 0) + (hasPhone ? 1 : 0) + Math.max(0, oauthCount - 1);
        if (otherLoginWays <= 0) {
            throw new DomainException("无法解绑：请至少保留一种登录方式");
        }
    }

    private String allocateOauthAccountName(OauthUserProfile profile) {
        String prefix = switch (profile.getProvider().toUpperCase(Locale.ROOT)) {
            case "GITHUB" -> "gh_";
            case "GITEE" -> "ge_";
            case "QQ" -> "qq_";
            default -> "wx_";
        };
        String suffix = profile.getOpenId().replaceAll("[^a-zA-Z0-9]", "");
        if (suffix.length() > 12) {
            suffix = suffix.substring(0, 12);
        }
        if (!StringUtils.hasText(suffix)) {
            suffix = String.valueOf(Math.abs(RANDOM.nextInt(1_000_000)));
        }
        String base = (prefix + suffix).toLowerCase(Locale.ROOT);
        String candidate = base;
        int i = 0;
        while (accountApi.findByIdentifier(candidate, "ACCOUNT") != null) {
            i++;
            candidate = base + i;
        }
        return candidate;
    }

    private List<OauthBindingResult> toResults(List<OauthBindingInfo> bindings) {
        List<OauthBindingResult> list = new ArrayList<>();
        for (OauthBindingInfo binding : bindings) {
            OauthBindingResult row = new OauthBindingResult();
            row.setProvider(binding.getProvider());
            row.setLabel(oauthClientPort.providerLabel(binding.getProvider()));
            row.setOpenIdMasked(maskOpenId(binding.getOpenId()));
            row.setNickname(binding.getNickname());
            row.setAvatar(binding.getAvatar());
            row.setBoundAt(binding.getBoundAt() == null ? null : binding.getBoundAt().toString());
            list.add(row);
        }
        return list;
    }

    private static String maskOpenId(String openId) {
        if (!StringUtils.hasText(openId)) {
            return "";
        }
        String value = openId.trim();
        if (value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }

    private String frontendCallback(AccountType accountType) {
        String key = accountType == AccountType.ADMIN
                ? "AUTH_OAUTH_FRONTEND_CALLBACK_ADMIN"
                : "AUTH_OAUTH_FRONTEND_CALLBACK_PORTAL";
        String configured = configApi.getByKey(key, "").trim();
        String candidate = StringUtils.hasText(configured)
                ? configured
                : joinBaseAndPath(
                        accountType == AccountType.ADMIN ? adminFrontendBaseUrl : portalFrontendBaseUrl,
                        "/auth/oauth/callback");
        if (!isAbsoluteHttpUrl(candidate)) {
            throw new DomainException("OAuth 前端回调地址必须为 http(s) 绝对 URL，请配置 " + key);
        }
        return candidate;
    }

    private static String joinBaseAndPath(String baseUrl, String path) {
        String base = baseUrl == null ? "" : baseUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String suffix = path == null ? "" : path.trim();
        if (!suffix.startsWith("/")) {
            suffix = "/" + suffix;
        }
        return base + suffix;
    }

    private static boolean isAbsoluteHttpUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String lower = value.trim().toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private String successRedirect(String frontend, LoginResult login, String redirect, String action) {
        StringBuilder url = new StringBuilder(frontend);
        url.append(frontend.contains("?") ? '&' : '?');
        url.append("oauth_status=ok&oauth_action=").append(enc(action));
        if (login != null && StringUtils.hasText(login.getToken())) {
            url.append("&oauth_code=").append(enc(oauthExchangePort.save(login)));
        }
        if (StringUtils.hasText(redirect)) {
            url.append("&redirect=").append(enc(redirect));
        }
        return url.toString();
    }

    private String failRedirect(String frontend, String message) {
        StringBuilder url = new StringBuilder(frontend);
        url.append(frontend.contains("?") ? '&' : '?');
        url.append("oauth_status=error&oauth_message=").append(enc(nullToEmpty(message)));
        return url.toString();
    }

    private static String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * OAuth 绑定展示结果。
     */
    public static class OauthBindingResult {
        private String provider;
        private String label;
        private String openIdMasked;
        private String nickname;
        private String avatar;
        private String boundAt;

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getOpenIdMasked() { return openIdMasked; }
        public void setOpenIdMasked(String openIdMasked) { this.openIdMasked = openIdMasked; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getBoundAt() { return boundAt; }
        public void setBoundAt(String boundAt) { this.boundAt = boundAt; }
    }
}

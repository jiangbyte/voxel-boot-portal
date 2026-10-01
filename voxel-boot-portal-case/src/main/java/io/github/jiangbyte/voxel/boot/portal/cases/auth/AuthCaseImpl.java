package io.github.jiangbyte.voxel.boot.portal.cases.auth;

import cn.hutool.core.util.IdUtil;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.CancelAccountCommand;
import io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port.MailSenderPort;
import io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port.SmsSenderPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordApi;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.AuthCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.CaptchaResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.PasswordKeyResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import lombok.RequiredArgsConstructor;

/**
 * 认证写用例：登录选项、验证码、密码密钥、登录/OTP、注册、刷新、找回/重置密码、注销。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AuthCaseImpl implements IAuthCase {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DEFAULT_OTP_TTL_SECONDS = 300;
    private static final String[] OAUTH_PROVIDERS = {
            "GITHUB", "GITEE", "QQ", "WECHAT_OPEN", "WECHAT_MP"
    };
    private static final String[] OAUTH_LABELS = {
            "GitHub", "Gitee", "QQ", "微信", "微信小程序"
    };
    private static final boolean[] OAUTH_WEB = {true, true, true, true, false};

    private final AccountApi accountApi;
    private final SessionPort sessionPort;
    private final AuthCryptoPort cryptoPort;
    private final ConfigApi configApi;
    private final MailSenderPort mailSenderPort;
    private final SmsSenderPort smsSenderPort;
    private final WeakPasswordApi weakPasswordApi;


    /** 签发图形验证码。 */
    public CaptchaResult captcha() {
        return cryptoPort.createCaptcha();
    }

    /** 签发密码传输 RSA 公钥。 */
    public PasswordKeyResult passwordKey() {
        return cryptoPort.createPasswordKey();
    }

    /** 登录页公开配置。 */
    public AuthOptionsResult authOptions(AccountType accountType) {
        // 1. 归一账号类型并读取开关
        AccountType type = accountType == null ? AccountType.ADMIN : accountType;
        String typeName = type.name();
        AuthOptionsResult result = new AuthOptionsResult();
        result.setAccountType(typeName);
        result.setAllowAccount(Flags.of(true));
        result.setAllowEmail(Flags.of(configBool("AUTH_LOGIN_" + typeName + "_ALLOW_EMAIL", true)));
        result.setAllowPhone(Flags.of(configBool("AUTH_LOGIN_" + typeName + "_ALLOW_PHONE", true)));
        result.setAllowOtp(Flags.of(configBool("AUTH_LOGIN_" + typeName + "_ALLOW_OTP", true)));
        boolean defaultRegister = type == AccountType.PORTAL;
        result.setRegisterEnabled(Flags.of(configBool("AUTH_REGISTER_" + typeName + "_ENABLED", defaultRegister)));
        if (type == AccountType.PORTAL) {
            result.setRegisterAllowAccount(Flags.of(configBool("AUTH_REGISTER_PORTAL_ALLOW_ACCOUNT", true)));
            result.setRegisterAllowEmail(Flags.of(configBool("AUTH_REGISTER_PORTAL_ALLOW_EMAIL", true)));
            result.setRegisterAllowPhone(Flags.of(configBool("AUTH_REGISTER_PORTAL_ALLOW_PHONE", false)));
        } else {
            result.setRegisterAllowAccount(Flags.of(false));
            result.setRegisterAllowEmail(Flags.of(false));
            result.setRegisterAllowPhone(Flags.of(false));
        }
        result.setForceBindEmail(Flags.of(configBool("AUTH_FORCE_BIND_" + typeName + "_EMAIL", false)));
        result.setForceBindPhone(Flags.of(configBool("AUTH_FORCE_BIND_" + typeName + "_PHONE", false)));
        result.setRegisterRequireEmail(Flags.of(configBool(
                "AUTH_REGISTER_" + typeName + "_REQUIRE_EMAIL", type == AccountType.PORTAL)));
        result.setRegisterRequirePhone(Flags.of(configBool("AUTH_REGISTER_" + typeName + "_REQUIRE_PHONE", false)));
        // 2. OAuth 提供商列表
        result.setOauthProviders(buildOauthProviderOptions(type));
        result.setPasswordChangeVerifyMethod(
                configApi.getByKey("PASSWORD_CHANGE_VERIFY_METHOD", "OLD_PASSWORD").trim().toUpperCase(Locale.ROOT));
        // 3. 页脚
        AuthOptionsResult.SiteFooterSnapshot footer = resolveFooter();
        result.setSiteFooter(footer);
        result.setCopyrightText(footer.getCopyrightText());
        result.setCopyrightUrl(footer.getCopyrightUrl());
        return result;
    }

    /** 管理端密码/OTP 登录。 */
    @Transactional
    public LoginResult adminLogin(LoginCommand command) {
        return login(command, AccountType.ADMIN);
    }

    /** 门户端密码/OTP 登录。 */
    @Transactional
    public LoginResult portalLogin(LoginCommand command) {
        return login(command, AccountType.PORTAL);
    }

    /** 兼容旧入口：账号 + 明文密码。 */
    @Transactional
    public LoginResult adminLogin(String username, String password) {
        LoginCommand command = new LoginCommand();
        command.setAccount(username);
        command.setPassword(password);
        command.setLoginMode("PASSWORD");
        command.setIdentityType("ACCOUNT");
        command.setRememberMe(Flags.of(true));
        return login(command, AccountType.ADMIN);
    }

    /** 兼容旧入口：账号 + 明文密码。 */
    @Transactional
    public LoginResult portalLogin(String username, String password) {
        LoginCommand command = new LoginCommand();
        command.setAccount(username);
        command.setPassword(password);
        command.setLoginMode("PASSWORD");
        command.setIdentityType("ACCOUNT");
        command.setRememberMe(Flags.of(true));
        return login(command, AccountType.PORTAL);
    }

    /** 为已有账号签发会话（OAuth 复用）。 */
    @Transactional
    public LoginResult issueLoginForAccount(AccountInfo account, AccountType accountType, String loginLabel) {
        if (account == null || !AccountStatus.ENABLED.name().equalsIgnoreCase(account.getAccountStatus())) {
            throw new DomainException(401, "账号不可用");
        }
        if (!accountType.name().equalsIgnoreCase(account.getAccountType())) {
            throw new DomainException(401, "账号类型不匹配");
        }
        String label = StringUtils.hasText(loginLabel)
                ? loginLabel
                : accountApi.findIdentifier(account.getId(), "ACCOUNT");
        return issueSession(account, accountType, Flags.of(true), label);
    }

    /** 发送登录 OTP。 */
    public void sendLoginCode(SendCodeCommand request, AccountType accountType) {
        // 1. 校验验证码与通道
        cryptoPort.verifyCaptcha(request.getCaptchaId(), request.getCaptchaValue());
        AccountType type = accountType == null ? AccountType.ADMIN : accountType;
        String channel = normalizeChannel(request.getChannel());
        String identityType = "EMAIL".equals(channel) ? "EMAIL" : "PHONE";
        ensureIdentityAllowed(type, identityType, "OTP");
        String normalized = normalizeTarget(channel, request.getTarget());
        // 2. 账号不存在时静默返回，避免枚举
        AccountInfo account = accountApi.findByIdentifier(normalized, identityType);
        if (account == null) {
            return;
        }
        if (!type.name().equalsIgnoreCase(account.getAccountType())) {
            return;
        }
        // 3. 生成 OTP 入库并发送
        String code = sixDigitCode();
        Duration ttl = otpTtl();
        cryptoPort.storeLoginOtp(type.name(), channel, normalized, code, ttl);
        sendCodeMailOrSms(channel, normalized, "LOGIN_CODE", code, ttl);
    }

    /** 发送门户注册 OTP。 */
    public void sendRegisterCode(SendCodeCommand request) {
        // 1. 校验验证码与注册开关
        cryptoPort.verifyCaptcha(request.getCaptchaId(), request.getCaptchaValue());
        if (!configBool("AUTH_REGISTER_PORTAL_ENABLED", true)) {
            throw new DomainException("门户注册已关闭");
        }
        String channel = normalizeChannel(request.getChannel());
        ensureRegisterChannelAllowed(channel);
        String normalized = normalizeTarget(channel, request.getTarget());
        String identityType = "EMAIL".equals(channel) ? "EMAIL" : "PHONE";
        // 2. 唯一性校验
        if (accountApi.findByIdentifier(normalized, identityType) != null) {
            throw new DomainException("EMAIL".equals(channel) ? "邮箱已被使用" : "手机号已被使用");
        }
        // 3. 存 OTP 并发送
        String code = sixDigitCode();
        Duration ttl = otpTtl();
        cryptoPort.storeRegisterOtp(channel, normalized, code, ttl);
        sendCodeMailOrSms(channel, normalized, "LOGIN_CODE", code, ttl);
    }

    /** 门户注册。 */
    @Transactional
    public RegisterResult registerPortal(RegisterCommand request) {
        // 1. 开关与验证码
        if (!configBool("AUTH_REGISTER_PORTAL_ENABLED", true)) {
            throw new DomainException("门户注册已关闭");
        }
        cryptoPort.verifyCaptcha(request.getCaptchaId(), request.getCaptchaValue());
        String channel = normalizeRegisterChannel(request.getRegisterChannel());
        ensureRegisterChannelAllowed(channel);

        // 2. 按通道解析账号/邮箱/手机并消费 OTP
        String accountName;
        String email = null;
        String phone = null;
        if ("ACCOUNT".equals(channel)) {
            accountName = requireAccountName(request.getAccount());
            if (accountApi.findByIdentifier(accountName, "ACCOUNT") != null) {
                throw new DomainException("账号已存在");
            }
            email = StringUtils.hasText(request.getEmail())
                    ? request.getEmail().trim().toLowerCase(Locale.ROOT) : null;
            phone = StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null;
            if (configBool("AUTH_REGISTER_PORTAL_REQUIRE_EMAIL", true) && !StringUtils.hasText(email)) {
                throw new DomainException("注册必填邮箱");
            }
            if (configBool("AUTH_REGISTER_PORTAL_REQUIRE_PHONE", false) && !StringUtils.hasText(phone)) {
                throw new DomainException("注册必填手机号");
            }
        } else if ("EMAIL".equals(channel)) {
            email = normalizeTarget("EMAIL", request.getEmail());
            if (!StringUtils.hasText(request.getOtpCode())) {
                throw new DomainException("请输入邮箱验证码");
            }
            if (!cryptoPort.consumeRegisterOtp("EMAIL", email, request.getOtpCode().trim())) {
                throw new DomainException("验证码无效或已过期");
            }
            if (accountApi.findByIdentifier(email, "EMAIL") != null) {
                throw new DomainException("邮箱已被使用");
            }
            accountName = allocateAccountFromEmail(email);
        } else {
            phone = normalizeTarget("PHONE", request.getPhone());
            if (!StringUtils.hasText(request.getOtpCode())) {
                throw new DomainException("请输入手机验证码");
            }
            if (!cryptoPort.consumeRegisterOtp("PHONE", phone, request.getOtpCode().trim())) {
                throw new DomainException("验证码无效或已过期");
            }
            if (accountApi.findByIdentifier(phone, "PHONE") != null) {
                throw new DomainException("手机号已被使用");
            }
            accountName = allocateAccountFromPhone(phone);
        }

        // 3. 解密密码、策略校验、建号
        String rawPassword = resolveRawPassword(request.getPasswordKeyId(), request.getPassword());
        assertPasswordValid(rawPassword, accountName);
        AccountInfo account = accountApi.createPortalAccount(
                accountName, email, accountApi.encodePassword(rawPassword));
        if (StringUtils.hasText(phone)) {
            accountApi.upsertIdentity(account.getId(), "PHONE", phone, true);
        }

        // 4. 尽力发送注册成功通知
        if (StringUtils.hasText(email)) {
            try {
                mailSenderPort.sendTemplated("REGISTER_SUCCESS", email, Map.of(
                        "app_name", appName(), "account", accountName));
            } catch (Exception ignored) {
            }
        }
        if (StringUtils.hasText(phone)) {
            try {
                smsSenderPort.sendTemplated("REGISTER_SUCCESS", phone, Map.of(
                        "app_name", appName(), "account", accountName));
            } catch (Exception ignored) {
            }
        }
        return new RegisterResult(account.getId(), accountName, AccountType.PORTAL.name());
    }

    /** 忘记密码（邮件链接）。 */
    public void forgotPassword(String email, String captchaId, String captchaValue, AccountType accountType) {
        // 1. 校验验证码
        cryptoPort.verifyCaptcha(captchaId, captchaValue);
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        // 2. 静默：账号不存在或类型不匹配
        AccountInfo account = accountApi.findByIdentifier(normalized, "EMAIL");
        if (account == null || !accountType.name().equalsIgnoreCase(account.getAccountType())) {
            return;
        }
        // 3. 写重置令牌并发邮件
        long ttlSeconds = Math.max(60L, configLong("AUTH_PASSWORD_RESET_TOKEN_TTL_SECONDS", 600));
        String token = IdUtil.simpleUUID();
        cryptoPort.storeResetToken(token, account.getId(), Duration.ofSeconds(ttlSeconds));
        String resetLink = buildPasswordResetLink(token, accountType);
        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("app_name", appName());
        vars.put("reset_link", resetLink);
        vars.put("email", normalized);
        vars.put("expire_minutes", String.valueOf(Math.max(1, ttlSeconds / 60)));
        mailSenderPort.sendTemplated("RESET_PASSWORD_CODE", normalized, vars);
    }

    /** 忘记密码（手机 OTP）。 */
    public void forgotPasswordByPhone(String phone, String captchaId, String captchaValue, AccountType accountType) {
        cryptoPort.verifyCaptcha(captchaId, captchaValue);
        String normalized = normalizeTarget("PHONE", phone);
        AccountInfo account = accountApi.findByIdentifier(normalized, "PHONE");
        if (account == null || !accountType.name().equalsIgnoreCase(account.getAccountType())) {
            return;
        }
        String code = sixDigitCode();
        Duration ttl = otpTtl();
        cryptoPort.storeResetPasswordOtp(accountType.name(), normalized, code, ttl);
        sendCodeMailOrSms("PHONE", normalized, "RESET_PASSWORD_CODE", code, ttl);
    }

    /** 通过邮件令牌重置密码。 */
    @Transactional
    public void resetPassword(String token, String passwordKeyId, String password,
                              String captchaId, String captchaValue, AccountType accountType) {
        cryptoPort.verifyCaptcha(captchaId, captchaValue);
        String rawPassword = resolveRawPassword(passwordKeyId, password);
        String accountId = cryptoPort.consumeResetToken(token);
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException("重置令牌无效或已过期");
        }
        AccountInfo account = accountApi.getById(accountId);
        if (account == null || !accountType.name().equalsIgnoreCase(account.getAccountType())) {
            throw new DomainException("重置令牌无效");
        }
        String accountName = accountApi.findIdentifier(accountId, "ACCOUNT");
        assertPasswordValid(rawPassword, accountName);
        accountApi.updatePasswordHash(accountId, accountApi.encodePassword(rawPassword));
        notifyPasswordResetSuccess(accountId, accountName);
    }

    /** 通过手机 OTP 重置密码。 */
    @Transactional
    public void resetPasswordByPhone(String phone, String otpCode, String passwordKeyId, String password,
                                     String captchaId, String captchaValue, AccountType accountType) {
        cryptoPort.verifyCaptcha(captchaId, captchaValue);
        String normalized = normalizeTarget("PHONE", phone);
        if (!cryptoPort.consumeResetPasswordOtp(accountType.name(), normalized,
                otpCode == null ? "" : otpCode.trim())) {
            throw new DomainException("验证码无效或已过期");
        }
        AccountInfo account = accountApi.findByIdentifier(normalized, "PHONE");
        if (account == null || !accountType.name().equalsIgnoreCase(account.getAccountType())) {
            throw new DomainException("账号不存在");
        }
        String rawPassword = resolveRawPassword(passwordKeyId, password);
        String accountName = accountApi.findIdentifier(account.getId(), "ACCOUNT");
        assertPasswordValid(rawPassword, accountName);
        accountApi.updatePasswordHash(account.getId(), accountApi.encodePassword(rawPassword));
        notifyPasswordResetSuccess(account.getId(), accountName);
    }

    /** 刷新当前会话。 */
    public LoginResult refreshSession() {
        // 1. 校验登录
        String accountId = sessionPort.currentAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        AccountInfo account = accountApi.getById(accountId);
        if (account == null) {
            throw new DomainException(401, "账号不存在或已失效");
        }
        // 2. 续期
        long renewTtl = configLong("AUTH_TOKEN_TTL_SECONDS", 0);
        SessionPort.RefreshSnapshot snapshot = sessionPort.refreshCurrent(renewTtl);
        if (snapshot == null) {
            throw new DomainException(401, "未登录");
        }
        // 3. 组装结果与强制绑定标记
        LoginResult result = new LoginResult(snapshot.token(), snapshot.accountId(), snapshot.accountType());
        result.setExpiresIn(snapshot.expiresIn() > 0 ? snapshot.expiresIn() : null);
        result.setPasswordExpired(Flags.of(false));
        applyForceBindFlags(result, AccountType.from(snapshot.accountType()), snapshot.accountId());
        return result;
    }

    public void logoutAdmin() {
        sessionPort.logoutAdmin();
    }

    public void logoutPortal() {
        sessionPort.logoutPortal();
    }

    @Transactional
    public void cancelAdmin(String cancelReason) {
        String accountId = sessionPort.currentAdminAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "未登录");
        }
        accountApi.cancel(new CancelAccountCommand(accountId, accountId, cancelReason));
        sessionPort.logoutAdmin();
    }

    @Transactional
    public void cancelPortal(String cancelReason) {
        String accountId = sessionPort.currentPortalAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "未登录");
        }
        accountApi.cancel(new CancelAccountCommand(accountId, accountId, cancelReason));
        sessionPort.logoutPortal();
    }

    private LoginResult login(LoginCommand request, AccountType expectedType) {
        // 1. 可选验证码：有传则校验
        if (StringUtils.hasText(request.getCaptchaId()) || StringUtils.hasText(request.getCaptchaValue())) {
            cryptoPort.verifyCaptcha(request.getCaptchaId(), request.getCaptchaValue());
        }
        String identityType = StringUtils.hasText(request.getIdentityType()) ? request.getIdentityType() : "ACCOUNT";
        String loginMode = StringUtils.hasText(request.getLoginMode())
                ? request.getLoginMode().trim().toUpperCase(Locale.ROOT) : "PASSWORD";
        ensureIdentityAllowed(expectedType, identityType, loginMode);

        // 2. OTP 或密码路径定位账号
        AccountInfo account;
        if ("OTP".equals(loginMode)) {
            String channel = "EMAIL".equalsIgnoreCase(identityType) ? "EMAIL" : "PHONE";
            String target = normalizeLoginAccount(identityType, request.getAccount());
            if (!cryptoPort.consumeLoginOtp(expectedType.name(), channel, target,
                    request.getOtpCode() == null ? "" : request.getOtpCode().trim())) {
                throw new DomainException(401, "账号或密码错误");
            }
            account = accountApi.findByIdentifier(target, identityType);
            if (account == null
                    || !AccountStatus.ENABLED.name().equalsIgnoreCase(account.getAccountStatus())
                    || !expectedType.name().equalsIgnoreCase(account.getAccountType())) {
                throw new DomainException(401, "账号或密码错误");
            }
        } else {
            if (!StringUtils.hasText(request.getPassword())) {
                throw new DomainException("请输入密码");
            }
            String rawPassword = resolveRawPassword(request.getPasswordKeyId(), request.getPassword());
            account = accountApi.findByIdentifier(request.getAccount(), identityType);
            if (account == null
                    || !AccountStatus.ENABLED.name().equalsIgnoreCase(account.getAccountStatus())
                    || !accountApi.matchesPassword(rawPassword, account.getPasswordHash())) {
                throw new DomainException(401, "账号或密码错误");
            }
            if (!expectedType.name().equalsIgnoreCase(account.getAccountType())) {
                throw new DomainException(401, "账号类型不匹配");
            }
        }
        // 3. 签发会话
        Integer rememberMe = Flags.of(
                request.getRememberMe() == null || Flags.isOn(request.getRememberMe()));
        return issueSession(account, expectedType, rememberMe, request.getAccount());
    }

    private LoginResult issueSession(AccountInfo account, AccountType accountType,
                                     Integer rememberMe, String loginAccount) {
        // 1. 解析 TTL（客户端 IP/UA 由会话基础设施在请求上下文中补齐时可扩展）
        long ttl = configLong("AUTH_TOKEN_TTL_SECONDS", 0);
        // 2. 登录
        String token = sessionPort.login(
                accountType.name(),
                account.getId(),
                StringUtils.hasText(loginAccount) ? loginAccount : account.getPrimaryIdentifier(),
                null, null, null, rememberMe, ttl);
        // 3. 刷新登录元数据
        accountApi.updateLoginMeta(account.getId(), null, LocalDateTime.now(), null);
        // 4. 组装结果（TTL 由 Sa-Token 侧管理，登录时不强制回写 expiresIn）
        LoginResult result = new LoginResult(token, account.getId(), accountType.name());
        result.setPasswordExpired(Flags.of(false));
        if (ttl > 0) {
            result.setExpiresIn(ttl);
        }
        applyForceBindFlags(result, accountType, account.getId());
        return result;
    }

    private void applyForceBindFlags(LoginResult result, AccountType accountType, String accountId) {
        boolean forceEmail = configBool("AUTH_FORCE_BIND_" + accountType.name() + "_EMAIL", false);
        boolean forcePhone = configBool("AUTH_FORCE_BIND_" + accountType.name() + "_PHONE", false);
        if (forceEmail && !StringUtils.hasText(accountApi.findIdentifier(accountId, "EMAIL"))) {
            result.setForceBindEmail(Flags.of(true));
        }
        if (forcePhone && !StringUtils.hasText(accountApi.findIdentifier(accountId, "PHONE"))) {
            result.setForceBindPhone(Flags.of(true));
        }
    }

    private List<AuthOptionsResult.OauthProviderOption> buildOauthProviderOptions(AccountType type) {
        List<AuthOptionsResult.OauthProviderOption> list = new ArrayList<>();
        for (int i = 0; i < OAUTH_PROVIDERS.length; i++) {
            if (type == AccountType.ADMIN && "WECHAT_MP".equals(OAUTH_PROVIDERS[i])) {
                continue;
            }
            AuthOptionsResult.OauthProviderOption item = new AuthOptionsResult.OauthProviderOption();
            item.setProvider(OAUTH_PROVIDERS[i]);
            item.setLabel(OAUTH_LABELS[i]);
            item.setWebOAuth(Flags.of(OAUTH_WEB[i]));
            item.setEnabled(Flags.of(configBool(
                    "AUTH_OAUTH_" + type.name() + "_" + OAUTH_PROVIDERS[i] + "_ENABLED", false)));
            list.add(item);
        }
        return list;
    }

    private AuthOptionsResult.SiteFooterSnapshot resolveFooter() {
        AuthOptionsResult.SiteFooterSnapshot footer = new AuthOptionsResult.SiteFooterSnapshot();
        footer.setCopyrightText(trim(configApi.getByKey("COPYRIGHT_TEXT", "")));
        footer.setCopyrightUrl(trim(configApi.getByKey("COPYRIGHT_URL", "")));
        footer.setIcpNumber(trim(configApi.getByKey("SITE_ICP_NUMBER", "")));
        footer.setIcpUrl(trim(configApi.getByKey("SITE_ICP_URL", "")));
        footer.setPsbNumber(trim(configApi.getByKey("SITE_PSB_NUMBER", "")));
        footer.setPsbUrl(trim(configApi.getByKey("SITE_PSB_URL", "")));
        return footer;
    }

    private void ensureIdentityAllowed(AccountType type, String identityType, String loginMode) {
        String id = identityType == null ? "ACCOUNT" : identityType.trim().toUpperCase(Locale.ROOT);
        if ("EMAIL".equals(id) && !configBool("AUTH_LOGIN_" + type.name() + "_ALLOW_EMAIL", true)) {
            throw new DomainException("未开放邮箱登录");
        }
        if ("PHONE".equals(id) && !configBool("AUTH_LOGIN_" + type.name() + "_ALLOW_PHONE", true)) {
            throw new DomainException("未开放手机登录");
        }
        if ("OTP".equalsIgnoreCase(loginMode)
                && !configBool("AUTH_LOGIN_" + type.name() + "_ALLOW_OTP", true)) {
            throw new DomainException("未开放验证码登录");
        }
    }

    private void ensureRegisterChannelAllowed(String channel) {
        if ("ACCOUNT".equals(channel) && !configBool("AUTH_REGISTER_PORTAL_ALLOW_ACCOUNT", true)) {
            throw new DomainException("未开放账号注册");
        }
        if ("EMAIL".equals(channel) && !configBool("AUTH_REGISTER_PORTAL_ALLOW_EMAIL", true)) {
            throw new DomainException("未开放邮箱注册");
        }
        if ("PHONE".equals(channel) && !configBool("AUTH_REGISTER_PORTAL_ALLOW_PHONE", false)) {
            throw new DomainException("未开放手机注册");
        }
    }

    private String resolveRawPassword(String passwordKeyId, String password) {
        if (StringUtils.hasText(passwordKeyId)) {
            return cryptoPort.decryptPassword(passwordKeyId, password);
        }
        return password;
    }

    private void assertPasswordValid(String rawPassword, String accountName) {
        if (!StringUtils.hasText(rawPassword) || rawPassword.length() < 6) {
            throw new DomainException("密码长度至少 6 位");
        }
        if (StringUtils.hasText(accountName) && rawPassword.equalsIgnoreCase(accountName)) {
            throw new DomainException("密码不能与账号相同");
        }
        if (weakPasswordApi != null && weakPasswordApi.contains(rawPassword)) {
            throw new DomainException("密码过于简单，请更换");
        }
    }

    private void sendCodeMailOrSms(String channel, String target, String scene, String code, Duration ttl) {
        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("code", code);
        vars.put("app_name", appName());
        vars.put("expire_minutes", String.valueOf(Math.max(1, ttl.toMinutes())));
        if ("EMAIL".equals(channel)) {
            mailSenderPort.sendTemplated(scene, target, vars);
        } else {
            smsSenderPort.sendTemplated(scene, target, vars);
        }
    }

    private void notifyPasswordResetSuccess(String accountId, String accountName) {
        String email = accountApi.findIdentifier(accountId, "EMAIL");
        String phone = accountApi.findIdentifier(accountId, "PHONE");
        Map<String, Object> vars = Map.of("app_name", appName(),
                "account", accountName == null ? accountId : accountName);
        if (StringUtils.hasText(email)) {
            try {
                mailSenderPort.sendTemplated("RESET_PASSWORD_SUCCESS", email, vars);
            } catch (Exception ignored) {
            }
        }
        if (StringUtils.hasText(phone)) {
            try {
                smsSenderPort.sendTemplated("RESET_PASSWORD_SUCCESS", phone, vars);
            } catch (Exception ignored) {
            }
        }
    }

    private String buildPasswordResetLink(String token, AccountType accountType) {
        String key = "AUTH_PASSWORD_RESET_URL_" + accountType.name();
        String base = configApi.getByKey(key, "").trim();
        if (!StringUtils.hasText(base)) {
            throw new DomainException("缺少系统配置: " + key);
        }
        String separator = base.contains("?") ? "&" : "?";
        return base + separator + "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    private Duration otpTtl() {
        long seconds = Math.max(60L, configLong("AUTH_OTP_TTL_SECONDS", DEFAULT_OTP_TTL_SECONDS));
        return Duration.ofSeconds(seconds);
    }

    private String sixDigitCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String normalizeChannel(String channel) {
        String value = channel == null ? "" : channel.trim().toUpperCase(Locale.ROOT);
        if (!"EMAIL".equals(value) && !"PHONE".equals(value)) {
            throw new DomainException("不支持的通道");
        }
        return value;
    }

    private String normalizeRegisterChannel(String channel) {
        String value = channel == null ? "" : channel.trim().toUpperCase(Locale.ROOT);
        if (!"ACCOUNT".equals(value) && !"EMAIL".equals(value) && !"PHONE".equals(value)) {
            throw new DomainException("不支持的注册通道");
        }
        return value;
    }

    private String normalizeTarget(String channel, String target) {
        if (!StringUtils.hasText(target)) {
            throw new DomainException("EMAIL".equals(channel) ? "请输入邮箱" : "请输入手机号");
        }
        String value = target.trim();
        if ("EMAIL".equals(channel)) {
            return value.toLowerCase(Locale.ROOT);
        }
        return value;
    }

    private String normalizeLoginAccount(String identityType, String account) {
        if (!StringUtils.hasText(account)) {
            throw new DomainException("请输入账号");
        }
        if ("EMAIL".equalsIgnoreCase(identityType)) {
            return account.trim().toLowerCase(Locale.ROOT);
        }
        return account.trim();
    }

    private String requireAccountName(String account) {
        if (!StringUtils.hasText(account)) {
            throw new DomainException("请输入账号");
        }
        String value = account.trim();
        if (!value.matches("^[a-zA-Z0-9_]{3,64}$")) {
            throw new DomainException("账号仅允许字母、数字和下划线，长度 3-64");
        }
        return value;
    }

    private String allocateAccountFromEmail(String email) {
        String local = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        String base = ("u_" + local.replaceAll("[^a-zA-Z0-9_]", "")).toLowerCase(Locale.ROOT);
        if (base.length() > 24) {
            base = base.substring(0, 24);
        }
        return allocateUniqueAccount(base);
    }

    private String allocateAccountFromPhone(String phone) {
        String suffix = phone.replaceAll("\\D", "");
        if (suffix.length() > 8) {
            suffix = suffix.substring(suffix.length() - 8);
        }
        return allocateUniqueAccount("p_" + suffix);
    }

    private String allocateUniqueAccount(String base) {
        String candidate = base;
        int i = 0;
        while (accountApi.findByIdentifier(candidate, "ACCOUNT") != null) {
            i++;
            candidate = base + i;
        }
        return candidate;
    }

    private String appName() {
        return configApi.getByKey("APP_NAME", "Voxel");
    }

    private boolean configBool(String key, boolean defaultValue) {
        String value = configApi.getByKey(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        String v = value.trim().toLowerCase(Locale.ROOT);
        return "true".equals(v) || "1".equals(v) || "yes".equals(v);
    }

    private long configLong(String key, long defaultValue) {
        String value = configApi.getByKey(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * 登录命令。
     */
    public static class LoginCommand {
        private String account;
        private String password;
        private String passwordKeyId;
        private String captchaId;
        private String captchaValue;
        private String identityType = "ACCOUNT";
        private String loginMode = "PASSWORD";
        private String otpCode;
        private Integer rememberMe = 1;

        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPasswordKeyId() { return passwordKeyId; }
        public void setPasswordKeyId(String passwordKeyId) { this.passwordKeyId = passwordKeyId; }
        public String getCaptchaId() { return captchaId; }
        public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
        public String getCaptchaValue() { return captchaValue; }
        public void setCaptchaValue(String captchaValue) { this.captchaValue = captchaValue; }
        public String getIdentityType() { return identityType; }
        public void setIdentityType(String identityType) { this.identityType = identityType; }
        public String getLoginMode() { return loginMode; }
        public void setLoginMode(String loginMode) { this.loginMode = loginMode; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public Integer getRememberMe() { return rememberMe; }
        public void setRememberMe(Integer rememberMe) { this.rememberMe = rememberMe; }
    }

    /**
     * 发送验证码命令。
     */
    public static class SendCodeCommand {
        private String target;
        private String channel;
        private String captchaId;
        private String captchaValue;

        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public String getCaptchaId() { return captchaId; }
        public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
        public String getCaptchaValue() { return captchaValue; }
        public void setCaptchaValue(String captchaValue) { this.captchaValue = captchaValue; }
    }

    /**
     * 注册命令。
     */
    public static class RegisterCommand {
        private String registerChannel;
        private String account;
        private String email;
        private String phone;
        private String otpCode;
        private String password;
        private String passwordKeyId;
        private String captchaId;
        private String captchaValue;

        public String getRegisterChannel() { return registerChannel; }
        public void setRegisterChannel(String registerChannel) { this.registerChannel = registerChannel; }
        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPasswordKeyId() { return passwordKeyId; }
        public void setPasswordKeyId(String passwordKeyId) { this.passwordKeyId = passwordKeyId; }
        public String getCaptchaId() { return captchaId; }
        public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
        public String getCaptchaValue() { return captchaValue; }
        public void setCaptchaValue(String captchaValue) { this.captchaValue = captchaValue; }
    }
}

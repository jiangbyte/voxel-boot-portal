package io.github.jiangbyte.voxel.boot.portal.cases.auth;

import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.admin.ProfileUserAdminApi;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.ProfileUserPortalApi;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.AuthCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import lombok.RequiredArgsConstructor;

/**
 * 资料中心改密/换绑凭证用例：复用 Redis OTP，本地开发仅写缓存不发真实短信邮件。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AuthCredentialCaseImpl implements IAuthCredentialCase {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Duration OTP_TTL = Duration.ofSeconds(300);
    /** 本地默认改密校验：旧密码；可通过系统属性覆盖为 EMAIL_CODE/PHONE_CODE。 */
    private static final String CHANGE_VERIFY_METHOD =
            System.getProperty("voxel.auth.password-change-verify-method", "OLD_PASSWORD").toUpperCase(Locale.ROOT);

    private final AccountApi accountApi;
    private final AuthCryptoPort authCryptoPort;
    private final SessionPort sessionPort;
    private final ProfileUserAdminApi profileUserAdminApi;
    private final ProfileUserPortalApi profileUserPortalApi;


    /**
     * 发送改密验证码（EMAIL_CODE/PHONE_CODE 模式）。
     */
    public void sendChangePasswordCode(AccountType accountType) {
        // 1. 解析当前账号
        String accountId = requireAccountId(accountType);
        String method = CHANGE_VERIFY_METHOD;
        if (!"EMAIL_CODE".equals(method) && !"PHONE_CODE".equals(method)) {
            throw new DomainException("当前改密方式不使用验证码");
        }
        // 2. 定位绑定联系方式
        String channel = "EMAIL_CODE".equals(method) ? "EMAIL" : "PHONE";
        String target = accountApi.findIdentifier(accountId, channel);
        if (target == null || target.isBlank()) {
            throw new DomainException("账号未绑定可用于校验的联系方式");
        }
        // 3. 生成 OTP 入 Redis（本地仅打日志，不发真实通道）
        String code = sixDigitCode();
        authCryptoPort.storeChangePasswordOtp(accountType.name(), channel, accountId, code, OTP_TTL);
    }

    /**
     * 修改当前用户密码。
     */
    @Transactional
    public void updateCurrentPassword(AccountType accountType,
                                      String passwordKeyId,
                                      String oldPassword,
                                      String newPassword,
                                      String otpCode) {
        // 1. 解密新旧密码
        String accountId = requireAccountId(accountType);
        String rawOld = blankToNull(decryptOptional(passwordKeyId, oldPassword));
        String rawNew = authCryptoPort.decryptPassword(passwordKeyId, newPassword);
        if (rawNew == null || rawNew.isBlank()) {
            throw new DomainException("请输入新密码");
        }
        // 2. 加载账号并按配置校验旧密码或 OTP
        AccountInfo account = accountApi.getById(accountId);
        if (account == null) {
            throw new DomainException(404, "账号不存在");
        }
        verifyChangePassword(account, accountType, rawOld, otpCode);
        // 3. 更新密码哈希
        accountApi.updatePasswordHash(accountId, accountApi.encodePassword(rawNew));
    }

    /**
     * 发送绑定手机验证码。
     */
    public void sendBindPhoneCode(AccountType accountType, String target) {
        String accountId = requireAccountId(accountType);
        String normalized = normalizeTarget("PHONE", target);
        AccountInfo other = accountApi.findByIdentifier(normalized, "PHONE");
        if (other != null && !accountId.equals(other.getId())) {
            throw new DomainException("手机号已被使用");
        }
        String code = sixDigitCode();
        authCryptoPort.storeBindOtp(accountType.name(), "PHONE", accountId, normalized, code, OTP_TTL);
    }

    /**
     * 发送绑定邮箱验证码。
     */
    public void sendBindEmailCode(AccountType accountType, String target) {
        String accountId = requireAccountId(accountType);
        String normalized = normalizeTarget("EMAIL", target);
        AccountInfo other = accountApi.findByIdentifier(normalized, "EMAIL");
        if (other != null && !accountId.equals(other.getId())) {
            throw new DomainException("邮箱已被使用");
        }
        String code = sixDigitCode();
        authCryptoPort.storeBindOtp(accountType.name(), "EMAIL", accountId, normalized, code, OTP_TTL);
    }

    /**
     * 更新当前手机号。
     */
    @Transactional
    public void updateCurrentPhone(AccountType accountType,
                                   String passwordKeyId,
                                   String password,
                                   String phone,
                                   Integer phoneLoginEnabled,
                                   String otpCode) {
        // 1. 校验登录密码
        String accountId = requireAccountId(accountType);
        String rawPassword = authCryptoPort.decryptPassword(passwordKeyId, password);
        AccountInfo account = accountApi.getById(accountId);
        if (account == null) {
            throw new DomainException(404, "账号不存在");
        }
        if (!accountApi.matchesPassword(rawPassword, account.getPasswordHash())) {
            throw new DomainException("密码错误");
        }
        // 2. 变更手机时校验 OTP
        if (Flags.isOn(phoneLoginEnabled) && (phone == null || phone.isBlank())) {
            throw new DomainException("开启手机登录需填写手机号");
        }
        String normalized = blankToNull(phone == null ? null : phone.trim());
        String current = accountApi.findIdentifier(accountId, "PHONE");
        boolean needsOtp = normalized != null && !normalized.equals(nullToEmpty(current));
        if (needsOtp) {
            if (otpCode == null || otpCode.isBlank()) {
                throw new DomainException("请输入手机验证码");
            }
            if (!authCryptoPort.consumeBindOtp(accountType.name(), "PHONE", accountId, normalized, otpCode.trim())) {
                throw new DomainException("验证码无效或已过期");
            }
        }
        // 3. 更新身份并同步资料
        accountApi.upsertIdentity(accountId, "PHONE", normalized, Flags.isOn(phoneLoginEnabled));
        if (accountType == AccountType.ADMIN) {
            profileUserAdminApi.updatePhone(accountId, normalized);
        } else {
            profileUserPortalApi.updatePhone(accountId, normalized);
        }
    }

    /**
     * 更新当前邮箱。
     */
    @Transactional
    public void updateCurrentEmail(AccountType accountType,
                                   String passwordKeyId,
                                   String password,
                                   String email,
                                   Integer emailLoginEnabled,
                                   String otpCode) {
        // 1. 校验登录密码
        String accountId = requireAccountId(accountType);
        String rawPassword = authCryptoPort.decryptPassword(passwordKeyId, password);
        AccountInfo account = accountApi.getById(accountId);
        if (account == null) {
            throw new DomainException(404, "账号不存在");
        }
        if (!accountApi.matchesPassword(rawPassword, account.getPasswordHash())) {
            throw new DomainException("密码错误");
        }
        // 2. 变更邮箱时校验 OTP
        if (Flags.isOn(emailLoginEnabled) && (email == null || email.isBlank())) {
            throw new DomainException("开启邮箱登录需填写邮箱");
        }
        String normalized = email == null || email.isBlank() ? null : email.trim().toLowerCase(Locale.ROOT);
        String current = accountApi.findIdentifier(accountId, "EMAIL");
        boolean needsOtp = normalized != null && !normalized.equalsIgnoreCase(nullToEmpty(current));
        if (needsOtp) {
            if (otpCode == null || otpCode.isBlank()) {
                throw new DomainException("请输入邮箱验证码");
            }
            if (!authCryptoPort.consumeBindOtp(accountType.name(), "EMAIL", accountId, normalized, otpCode.trim())) {
                throw new DomainException("验证码无效或已过期");
            }
        }
        // 3. 更新身份并同步资料
        accountApi.upsertIdentity(accountId, "EMAIL", normalized, Flags.isOn(emailLoginEnabled));
        if (accountType == AccountType.ADMIN) {
            profileUserAdminApi.updateEmail(accountId, normalized);
        } else {
            profileUserPortalApi.updateEmail(accountId, normalized);
        }
    }

    private void verifyChangePassword(AccountInfo account, AccountType accountType, String oldPassword, String otpCode) {
        String method = CHANGE_VERIFY_METHOD;
        if ("OLD_PASSWORD".equals(method)) {
            if (oldPassword == null || oldPassword.isBlank()
                    || !accountApi.matchesPassword(oldPassword, account.getPasswordHash())) {
                throw new DomainException("旧密码不正确");
            }
            return;
        }
        if ("EMAIL_CODE".equals(method) || "PHONE_CODE".equals(method)) {
            if (otpCode == null || otpCode.isBlank()) {
                throw new DomainException("请输入验证码");
            }
            String channel = "EMAIL_CODE".equals(method) ? "EMAIL" : "PHONE";
            if (!authCryptoPort.consumeChangePasswordOtp(accountType.name(), channel, account.getId(), otpCode.trim())) {
                throw new DomainException("验证码无效或已过期");
            }
            return;
        }
        throw new DomainException("不支持的改密校验方式: " + method);
    }

    private String requireAccountId(AccountType accountType) {
        String accountId = accountType == AccountType.ADMIN
                ? sessionPort.currentAdminAccountId()
                : sessionPort.currentPortalAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "Not logged in");
        }
        return accountId;
    }

    private String decryptOptional(String passwordKeyId, String encrypted) {
        if (encrypted == null || encrypted.isBlank()) {
            return null;
        }
        return authCryptoPort.decryptPassword(passwordKeyId, encrypted);
    }

    private static String sixDigitCode() {
        return String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private static String normalizeTarget(String channel, String target) {
        if (target == null || target.isBlank()) {
            throw new DomainException("请填写接收目标");
        }
        return "EMAIL".equals(channel) ? target.trim().toLowerCase(Locale.ROOT) : target.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    private static String nullToEmpty(String text) {
        return text == null ? "" : text;
    }
}

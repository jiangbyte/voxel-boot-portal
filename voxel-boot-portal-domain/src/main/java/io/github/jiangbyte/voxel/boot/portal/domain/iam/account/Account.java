package io.github.jiangbyte.voxel.boot.portal.domain.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.event.AccountCancelled;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.event.AccountCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.event.PasswordChanged;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 账号聚合根：维护密码哈希、类型/状态、登录元数据与登录身份集合。
 *
 * Author: Charlie
 */
public class Account extends AggregateRoot<String> {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String id;
    private String passwordHash;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private String cancelReason;
    private String cancelNotifyEmail;
    private String cancelNotifyPhone;
    private String lastLoginIp;
    private String lastLoginAddress;
    private LocalDateTime lastLoginTime;
    private String lastLoginDevice;
    private String latestLoginIp;
    private String latestLoginAddress;
    private LocalDateTime latestLoginTime;
    private String latestLoginDevice;
    private final List<AccountIdentity> identities = new ArrayList<>();

    private Account() {
    }

    /**
     * 创建管理端账号（主身份为 ACCOUNT）。
     */
    public static Account createAdmin(String passwordHash, String accountIdentifier) {
        // 1. 校验密码哈希与登录标识
        String hash = requireText(passwordHash, "Password hash must not be blank");
        String login = requireText(accountIdentifier, "Account identifier must not be blank");
        // 2. 组装 ADMIN/ENABLED 聚合
        Account account = newBlank(AccountType.ADMIN, hash);
        // 3. 绑定主登录身份
        account.identities.add(AccountIdentity.create("ACCOUNT", login, Flags.of(true), Flags.of(true)));
        // 4. 登记创建事件
        account.registerEvent(new AccountCreated(account.id, AccountType.ADMIN.name()));
        return account;
    }

    /**
     * 创建门户账号；email 可选，非空时追加 EMAIL 身份。
     */
    public static Account createPortal(String passwordHash, String accountIdentifier, String email) {
        // 1. 校验密码哈希与登录标识
        String hash = requireText(passwordHash, "Password hash must not be blank");
        String login = requireText(accountIdentifier, "Account identifier must not be blank");
        // 2. 组装 PORTAL/ENABLED 聚合并绑定 ACCOUNT 身份
        Account account = newBlank(AccountType.PORTAL, hash);
        account.identities.add(AccountIdentity.create("ACCOUNT", login, Flags.of(true), Flags.of(true)));
        // 3. 可选绑定邮箱身份（归一化为小写）
        if (email != null && !email.isBlank()) {
            account.identities.add(AccountIdentity.create(
                    "EMAIL", email.trim().toLowerCase(Locale.ROOT), Flags.of(true), Flags.of(false)));
        }
        // 4. 登记创建事件
        account.registerEvent(new AccountCreated(account.id, AccountType.PORTAL.name()));
        return account;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Account reconstitute(String id,
                                       String passwordHash,
                                       String accountType,
                                       String accountStatus,
                                       LocalDateTime cancelledAt,
                                       String cancelledBy,
                                       String cancelReason,
                                       String cancelNotifyEmail,
                                       String cancelNotifyPhone,
                                       String lastLoginIp,
                                       String lastLoginAddress,
                                       LocalDateTime lastLoginTime,
                                       String lastLoginDevice,
                                       String latestLoginIp,
                                       String latestLoginAddress,
                                       LocalDateTime latestLoginTime,
                                       String latestLoginDevice,
                                       List<AccountIdentity> identities) {
        Account account = new Account();
        account.id = id;
        account.passwordHash = passwordHash;
        account.accountType = AccountType.from(accountType);
        account.accountStatus = AccountStatus.from(accountStatus);
        account.cancelledAt = cancelledAt;
        account.cancelledBy = cancelledBy;
        account.cancelReason = cancelReason;
        account.cancelNotifyEmail = cancelNotifyEmail;
        account.cancelNotifyPhone = cancelNotifyPhone;
        account.lastLoginIp = lastLoginIp;
        account.lastLoginAddress = lastLoginAddress;
        account.lastLoginTime = lastLoginTime;
        account.lastLoginDevice = lastLoginDevice;
        account.latestLoginIp = latestLoginIp;
        account.latestLoginAddress = latestLoginAddress;
        account.latestLoginTime = latestLoginTime;
        account.latestLoginDevice = latestLoginDevice;
        if (identities != null) {
            account.identities.addAll(identities);
        }
        return account;
    }

    /**
     * 注销账号：已注销则幂等返回；否则作废密码并写入注销元数据。
     */
    public void cancel(String reason, String cancelledBy, String notifyEmail, String notifyPhone) {
        // 1. 已注销则幂等退出，避免重复写库与重复事件
        if (this.accountStatus == AccountStatus.CANCELLED) {
            return;
        }
        // 2. 写入注销状态与通知快照
        LocalDateTime now = LocalDateTime.now();
        this.accountStatus = AccountStatus.CANCELLED;
        if (this.cancelledAt == null) {
            this.cancelledAt = now;
        }
        this.cancelledBy = cancelledBy;
        this.cancelReason = reason;
        this.cancelNotifyEmail = blankToNull(notifyEmail);
        this.cancelNotifyPhone = blankToNull(notifyPhone);
        // 3. 用随机令牌作废密码，防止旧凭据继续使用
        byte[] token = new byte[32];
        SECURE_RANDOM.nextBytes(token);
        this.passwordHash = "cancelled:" + Base64.getUrlEncoder().withoutPadding().encodeToString(token);
        // 4. 清空登录痕迹
        clearLoginMeta();
        // 5. 登记注销事件
        registerEvent(new AccountCancelled(this.id, cancelledBy, reason));
    }

    /**
     * 记录一次成功登录：将当前 latest 挪到 last，再写入新的 latest。
     */
    public void recordLogin(String ip, LocalDateTime time, String device) {
        // 1. 已注销账号禁止刷新登录元数据
        if (this.accountStatus == AccountStatus.CANCELLED) {
            throw new DomainException("Cancelled account cannot record login");
        }
        // 2. 将最近一次登录归档为上一次
        this.lastLoginIp = this.latestLoginIp;
        this.lastLoginAddress = this.latestLoginAddress;
        this.lastLoginTime = this.latestLoginTime;
        this.lastLoginDevice = this.latestLoginDevice;
        // 3. 写入本次登录
        this.latestLoginIp = ip;
        this.latestLoginTime = time == null ? LocalDateTime.now() : time;
        this.latestLoginDevice = device;
    }

    /**
     * 更换密码哈希并登记事件。
     */
    public void changePassword(String newHash) {
        // 1. 注销账号禁止改密
        if (this.accountStatus == AccountStatus.CANCELLED) {
            throw new DomainException("Cancelled account cannot change password");
        }
        // 2. 覆盖哈希
        this.passwordHash = requireText(newHash, "Password hash must not be blank");
        // 3. 登记密码变更事件
        registerEvent(new PasswordChanged(this.id));
    }

    /**
     * 更新账号状态（禁止改为 CANCELLED，注销走 cancel）。
     */
    public void changeStatus(String status) {
        AccountStatus next = AccountStatus.from(status);
        if (next == AccountStatus.CANCELLED) {
            throw new DomainException("Use cancel() to cancel account");
        }
        if (this.accountStatus == AccountStatus.CANCELLED) {
            throw new DomainException("Cancelled account cannot change status");
        }
        this.accountStatus = next;
    }

    /**
     * 更新或替换主 ACCOUNT 登录标识。
     */
    public void replaceAccountIdentifier(String accountIdentifier) {
        String login = requireText(accountIdentifier, "Account identifier must not be blank").trim();
        // 1. 查找主 ACCOUNT 身份；存在则改值，否则新建
        for (AccountIdentity identity : identities) {
            if ("ACCOUNT".equalsIgnoreCase(identity.getIdentityType())
                    && java.util.Objects.equals(identity.getIsPrimary(), 1)) {
                identity.changeIdentifier(login);
                return;
            }
        }
        identities.add(AccountIdentity.create("ACCOUNT", login, Flags.of(true), Flags.of(true)));
    }

    /**
     * 新增或更新指定类型身份；identifier 为空则移除该类型（非主 ACCOUNT）。
     */
    public void upsertIdentity(String identityType, String identifier, boolean loginEnabled) {
        // 1. 校验类型
        String type = requireText(identityType, "Identity type must not be blank").trim().toUpperCase(Locale.ROOT);
        if ("ACCOUNT".equals(type)) {
            throw new DomainException("Use replaceAccountIdentifier for ACCOUNT identity");
        }
        // 2. identifier 为空：移除该类型身份
        if (identifier == null || identifier.isBlank()) {
            identities.removeIf(item -> type.equalsIgnoreCase(item.getIdentityType()));
            return;
        }
        String value = "EMAIL".equals(type)
                ? identifier.trim().toLowerCase(Locale.ROOT)
                : identifier.trim();
        // 3. 存在则改值，否则新建（loginEnabled 映射为 verified）
        for (AccountIdentity identity : identities) {
            if (type.equalsIgnoreCase(identity.getIdentityType())) {
                identity.changeIdentifier(value);
                return;
            }
        }
        identities.add(AccountIdentity.create(type, value, Flags.of(loginEnabled), Flags.of(false)));
    }

    /**
     * 替换邮箱/手机次级登录身份（保留主 ACCOUNT 身份）。
     */
    public void replaceSecondaryLoginIdentities(Integer emailLoginEnabled, String email,
                                                Integer phoneLoginEnabled, String phone) {
        // 1. 邮箱：启用则 upsert，关闭则清空
        if (java.util.Objects.equals(emailLoginEnabled, 1)) {
            upsertIdentity("EMAIL", email, true);
        } else {
            upsertIdentity("EMAIL", null, false);
        }
        // 2. 手机：启用则 upsert，关闭则清空
        if (java.util.Objects.equals(phoneLoginEnabled, 1)) {
            upsertIdentity("PHONE", phone, true);
        } else {
            upsertIdentity("PHONE", null, false);
        }
    }

    /**
     * 物理删除前登记删除意图（应用层按需调用）。
     */
    public void markDeleted() {
        // 删除本身不产生专用事件时仍可扩展；当前仅作扩展点占位
    }

    private static Account newBlank(AccountType type, String passwordHash) {
        Account account = new Account();
        account.id = UUID.randomUUID().toString().replace("-", "");
        account.passwordHash = passwordHash;
        account.accountType = type;
        account.accountStatus = AccountStatus.ENABLED;
        return account;
    }

    private void clearLoginMeta() {
        this.lastLoginIp = null;
        this.lastLoginAddress = null;
        this.lastLoginTime = null;
        this.lastLoginDevice = null;
        this.latestLoginIp = null;
        this.latestLoginAddress = null;
        this.latestLoginTime = null;
        this.latestLoginDevice = null;
    }

    private static String requireText(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new DomainException(message);
        }
        return text;
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public String getCancelNotifyEmail() {
        return cancelNotifyEmail;
    }

    public String getCancelNotifyPhone() {
        return cancelNotifyPhone;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public String getLastLoginDevice() {
        return lastLoginDevice;
    }

    public String getLatestLoginIp() {
        return latestLoginIp;
    }

    public String getLatestLoginAddress() {
        return latestLoginAddress;
    }

    public LocalDateTime getLatestLoginTime() {
        return latestLoginTime;
    }

    public String getLatestLoginDevice() {
        return latestLoginDevice;
    }

    public List<AccountIdentity> getIdentities() {
        return Collections.unmodifiableList(identities);
    }
}

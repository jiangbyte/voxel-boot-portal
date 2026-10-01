package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.repository.AccountRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.PasswordHashingPort;
import lombok.RequiredArgsConstructor;

/**
 * 账号写用例：创建、更新、注销、删除，并实现跨上下文 {@link AccountApi}。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AccountCaseImpl implements IAccountCase, AccountApi {

    private final AccountRepository accountRepository;
    private final PasswordHashingPort passwordHashingPort;


    /**
     * 管理端创建账号。
     */
    @Transactional
    public void create(CreateAccountCommand command) {
        // 1. 校验登录标识唯一
        String login = requireLogin(command.account());
        ensureIdentifierUnique(login, "ACCOUNT", null);
        // 2. 禁止直接创建为注销状态
        if (command.accountStatus() != null
                && AccountStatus.CANCELLED == AccountStatus.from(command.accountStatus())) {
            throw new DomainException("注销状态不允许通过管理端设置");
        }
        // 3. 编码密码并按类型工厂创建
        String hash = passwordHashingPort.encode(requirePassword(command.rawPassword()));
        AccountType type = AccountType.from(command.accountType());
        Account account = type == AccountType.ADMIN
                ? Account.createAdmin(hash, login)
                : Account.createPortal(hash, login, command.email());
        // 4. 若指定非默认状态则覆盖（不含 CANCELLED）
        if (command.accountStatus() != null && !command.accountStatus().isBlank()) {
            account.changeStatus(command.accountStatus());
        }
        // 5. 持久化
        accountRepository.save(account);
    }

    /**
     * 管理端更新账号。
     */
    @Transactional
    public void update(UpdateAccountCommand command) {
        // 1. 加载聚合
        Account account = accountRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Account not found"));
        if (account.getAccountStatus() == AccountStatus.CANCELLED) {
            throw new DomainException("已注销账号不可更新");
        }
        // 2. 校验登录标识唯一并替换主身份
        String login = requireLogin(command.account());
        ensureIdentifierUnique(login, "ACCOUNT", account.getId());
        account.replaceAccountIdentifier(login);
        // 3. 可选改密
        if (command.rawPassword() != null && !command.rawPassword().isBlank()) {
            account.changePassword(passwordHashingPort.encode(command.rawPassword()));
        }
        // 4. 更新状态（禁止 CANCELLED）
        if (command.accountStatus() != null && !command.accountStatus().isBlank()) {
            account.changeStatus(command.accountStatus());
        }
        // 5. 保存
        accountRepository.save(account);
    }

    /**
     * 注销账号（幂等：已注销直接返回）。
     */
    @Override
    @Transactional
    public void cancel(CancelAccountCommand command) {
        // 1. 加载聚合
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new DomainException(404, "Account not found"));
        // 2. 从身份集合提取通知联系方式
        String email = findIdentifier(account, "EMAIL");
        String phone = findIdentifier(account, "PHONE");
        // 3. 领域注销（内部幂等）
        account.cancel(command.cancelReason(), command.cancelledBy(), email, phone);
        // 4. 保存
        accountRepository.save(account);
    }

    /**
     * 批量物理删除账号。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载后移除（仓储侧同步清理身份）
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            accountRepository.findById(id).ifPresent(account -> {
                account.markDeleted();
                accountRepository.remove(account);
            });
        }
    }

    @Override
    public AccountInfo findByIdentifier(String identifier, String identityType) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        String type = identityType == null || identityType.isBlank() ? "ACCOUNT" : identityType;
        return accountRepository.findByIdentifier(identifier.trim(), type)
                .map(this::toInfo)
                .orElse(null);
    }

    @Override
    public AccountInfo getById(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return null;
        }
        return accountRepository.findById(accountId).map(this::toInfo).orElse(null);
    }

    @Override
    public String findIdentifier(String accountId, String identityType) {
        if (accountId == null || accountId.isBlank() || identityType == null || identityType.isBlank()) {
            return null;
        }
        return accountRepository.findById(accountId)
                .map(account -> findIdentifier(account, identityType))
                .orElse(null);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String passwordHash) {
        if (rawPassword == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        return passwordHashingPort.matches(rawPassword, passwordHash);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordHashingPort.encode(requirePassword(rawPassword));
    }

    @Override
    public String getPasswordHash(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return null;
        }
        return accountRepository.findById(accountId)
                .map(Account::getPasswordHash)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updatePasswordHash(String accountId, String encodedPassword) {
        // 1. 加载聚合
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new DomainException(404, "Account not found"));
        // 2. 更换哈希
        account.changePassword(encodedPassword);
        // 3. 保存
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void upsertIdentity(String accountId, String identityType, String identifier, boolean loginEnabled) {
        // 1. 加载聚合
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new DomainException(404, "Account not found"));
        // 2. 非空标识时做全局唯一校验
        if (identifier != null && !identifier.isBlank()) {
            String normalized = "EMAIL".equalsIgnoreCase(identityType)
                    ? identifier.trim().toLowerCase(Locale.ROOT)
                    : identifier.trim();
            ensureIdentifierUnique(normalized, identityType, accountId);
        }
        // 3. 聚合内 upsert 并保存
        account.upsertIdentity(identityType, identifier, loginEnabled);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void updateLoginMeta(String accountId, String ip, LocalDateTime time, String device) {
        // 1. 账号不存在则静默返回（登录路径容错）
        accountRepository.findById(accountId).ifPresent(account -> {
            // 2. 聚合记录登录元数据
            account.recordLogin(ip, time, device);
            // 3. 保存
            accountRepository.save(account);
        });
    }

    @Override
    @Transactional
    public AccountInfo createPortalAccount(String account, String email, String encodedPassword) {
        // 1. 校验标识唯一
        String login = requireLogin(account);
        ensureIdentifierUnique(login, "ACCOUNT", null);
        if (email != null && !email.isBlank()) {
            ensureIdentifierUnique(email.trim().toLowerCase(Locale.ROOT), "EMAIL", null);
        }
        // 2. 工厂创建门户账号（密码已编码）
        Account created = Account.createPortal(encodedPassword, login, email);
        // 3. 持久化并返回快照
        accountRepository.save(created);
        return toInfo(created);
    }

    private void ensureIdentifierUnique(String identifier, String identityType, String excludeAccountId) {
        accountRepository.findByIdentifier(identifier, identityType).ifPresent(existing -> {
            if (excludeAccountId == null || !excludeAccountId.equals(existing.getId())) {
                throw new DomainException("登录标识已存在");
            }
        });
    }

    private AccountInfo toInfo(Account account) {
        AccountInfo info = new AccountInfo();
        info.setId(account.getId());
        info.setAccountType(account.getAccountType().name());
        info.setAccountStatus(account.getAccountStatus().name());
        info.setPasswordHash(account.getPasswordHash());
        info.setLatestLoginIp(account.getLatestLoginIp());
        info.setLatestLoginTime(account.getLatestLoginTime());
        info.setLatestLoginDevice(account.getLatestLoginDevice());
        info.setCancelledAt(account.getCancelledAt());
        info.setCancelledBy(account.getCancelledBy());
        info.setCancelReason(account.getCancelReason());
        info.setPrimaryIdentifier(findIdentifier(account, "ACCOUNT"));
        return info;
    }

    private static String findIdentifier(Account account, String identityType) {
        for (AccountIdentity identity : account.getIdentities()) {
            if (identityType.equalsIgnoreCase(identity.getIdentityType())
                    && "BOUND".equalsIgnoreCase(identity.getBindStatus())) {
                return identity.getIdentifier();
            }
        }
        return null;
    }

    private static String requireLogin(String account) {
        if (account == null || account.isBlank()) {
            throw new DomainException("Account identifier must not be blank");
        }
        return account.trim();
    }

    private static String requirePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new DomainException("Password must not be blank");
        }
        return rawPassword;
    }
}

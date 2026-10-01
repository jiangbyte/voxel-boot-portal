package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.repository.AccountRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.PasswordHashingPort;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IAccountCase {
    void create(CreateAccountCommand command);
    void update(UpdateAccountCommand command);
    void cancel(CancelAccountCommand command);
    void delete(List<String> ids);
    AccountInfo findByIdentifier(String identifier, String identityType);
    AccountInfo getById(String accountId);
    String findIdentifier(String accountId, String identityType);
    boolean matchesPassword(String rawPassword, String passwordHash);
    String encodePassword(String rawPassword);
    String getPasswordHash(String accountId);
    void updatePasswordHash(String accountId, String encodedPassword);
    void upsertIdentity(String accountId, String identityType, String identifier, boolean loginEnabled);
    void updateLoginMeta(String accountId, String ip, LocalDateTime time, String device);
    AccountInfo createPortalAccount(String account, String email, String encodedPassword);
}

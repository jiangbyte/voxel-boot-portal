package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 账号聚合与持久化对象互转。
 *
 * Author: Charlie
 */
public final class AccountAssembler {

    private AccountAssembler() {
    }

    /**
     * 持久化对象转聚合。
     */
    public static Account toAggregate(AccountPO po, List<AccountIdentityPO> identityPos) {
        if (po == null) {
            return null;
        }
        List<AccountIdentity> identities = new ArrayList<>();
        if (identityPos != null) {
            for (AccountIdentityPO identityPo : identityPos) {
                identities.add(toIdentity(identityPo));
            }
        }
        return Account.reconstitute(
                po.getId(),
                po.getPasswordHash(),
                po.getAccountType(),
                po.getAccountStatus(),
                po.getCancelledAt(),
                po.getCancelledBy(),
                po.getCancelReason(),
                po.getCancelNotifyEmail(),
                po.getCancelNotifyPhone(),
                po.getLastLoginIp(),
                po.getLastLoginAddress(),
                po.getLastLoginTime(),
                po.getLastLoginDevice(),
                po.getLatestLoginIp(),
                po.getLatestLoginAddress(),
                po.getLatestLoginTime(),
                po.getLatestLoginDevice(),
                identities);
    }

    /**
     * 聚合转账号 PO。
     */
    public static AccountPO toPO(Account account) {
        if (account == null) {
            return null;
        }
        AccountPO po = new AccountPO();
        po.setId(account.getId());
        po.setPasswordHash(account.getPasswordHash());
        po.setAccountType(account.getAccountType() == null ? null : account.getAccountType().name());
        po.setAccountStatus(account.getAccountStatus() == null
                ? AccountStatus.ENABLED.name()
                : account.getAccountStatus().name());
        po.setCancelledAt(account.getCancelledAt());
        po.setCancelledBy(account.getCancelledBy());
        po.setCancelReason(account.getCancelReason());
        po.setCancelNotifyEmail(account.getCancelNotifyEmail());
        po.setCancelNotifyPhone(account.getCancelNotifyPhone());
        po.setLastLoginIp(account.getLastLoginIp());
        po.setLastLoginAddress(account.getLastLoginAddress());
        po.setLastLoginTime(account.getLastLoginTime());
        po.setLastLoginDevice(account.getLastLoginDevice());
        po.setLatestLoginIp(account.getLatestLoginIp());
        po.setLatestLoginAddress(account.getLatestLoginAddress());
        po.setLatestLoginTime(account.getLatestLoginTime());
        po.setLatestLoginDevice(account.getLatestLoginDevice());
        return po;
    }

    /**
     * 聚合身份转 PO。
     */
    public static AccountIdentityPO toIdentityPO(AccountIdentity identity, String accountId) {
        if (identity == null) {
            return null;
        }
        AccountIdentityPO po = new AccountIdentityPO();
        po.setId(identity.getId());
        po.setAccountId(accountId);
        po.setIdentityType(identity.getIdentityType());
        po.setIdentifier(identity.getIdentifier());
        po.setVerified(identity.getVerified());
        po.setIsPrimary(identity.getIsPrimary());
        po.setBindStatus(identity.getBindStatus());
        return po;
    }

    private static AccountIdentity toIdentity(AccountIdentityPO po) {
        return AccountIdentity.reconstitute(
                po.getId(),
                po.getIdentityType(),
                po.getIdentifier(),
                po.getVerified(),
                po.getIsPrimary(),
                po.getBindStatus());
    }
}

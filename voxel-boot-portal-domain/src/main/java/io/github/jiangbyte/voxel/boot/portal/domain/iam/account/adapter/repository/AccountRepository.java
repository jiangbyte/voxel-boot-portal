package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;

/**
 * 账号聚合仓储端口。
 *
 * Author: Charlie
 */
public interface AccountRepository extends Repository<Account, String> {

    /**
     * 按登录标识与身份类型查找账号。
     */
    Optional<Account> findByIdentifier(String identifier, String identityType);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Account aggregate);
}

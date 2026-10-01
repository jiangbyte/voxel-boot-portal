package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.repository.AccountRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountIdentityMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountIdentityPO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountPO;
import lombok.RequiredArgsConstructor;

/**
 * 账号聚合仓储实现：主表 + 身份表同步与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper accountMapper;
    private final AccountIdentityMapper accountIdentityMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Account> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        AccountPO po = accountMapper.selectById(id);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(AccountAssembler.toAggregate(po, loadIdentities(id)));
    }

    @Override
    public Optional<Account> findByIdentifier(String identifier, String identityType) {
        if (!StringUtils.hasText(identifier) || !StringUtils.hasText(identityType)) {
            return Optional.empty();
        }
        // 1. 按身份类型 + 标识定位账号 ID
        AccountIdentityPO identity = accountIdentityMapper.selectOne(Wrappers.<AccountIdentityPO>lambdaQuery()
                .eq(AccountIdentityPO::getIdentityType, identityType.trim().toUpperCase())
                .eq(AccountIdentityPO::getIdentifier, identifier.trim())
                .eq(AccountIdentityPO::getBindStatus, "BOUND")
                .last("LIMIT 1"));
        if (identity == null || !StringUtils.hasText(identity.getAccountId())) {
            return Optional.empty();
        }
        // 2. 加载完整聚合
        return findById(identity.getAccountId());
    }

    @Override
    public void save(Account aggregate) {
        // 1. 聚合转主表 PO
        AccountPO po = AccountAssembler.toPO(aggregate);
        // 2. 按主键判断插入或更新主表
        AccountPO existing = accountMapper.selectById(po.getId());
        if (existing == null) {
            accountMapper.insert(po);
        } else {
            accountMapper.updateById(po);
        }
        // 3. 全量替换身份集合（数量小，避免逐条 diff）
        replaceIdentities(aggregate);
        // 4. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        accountIdentityMapper.delete(Wrappers.<AccountIdentityPO>lambdaQuery()
                .eq(AccountIdentityPO::getAccountId, id));
        accountMapper.deleteById(id);
    }

    @Override
    public void remove(Account aggregate) {
        // 1. 删除身份与主表
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        remove(aggregate.getId());
        // 2. 派发已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    private List<AccountIdentityPO> loadIdentities(String accountId) {
        return accountIdentityMapper.selectList(Wrappers.<AccountIdentityPO>lambdaQuery()
                .eq(AccountIdentityPO::getAccountId, accountId));
    }

    private void replaceIdentities(Account aggregate) {
        // 1. 删除旧身份
        accountIdentityMapper.delete(Wrappers.<AccountIdentityPO>lambdaQuery()
                .eq(AccountIdentityPO::getAccountId, aggregate.getId()));
        // 2. 插入当前聚合内身份
        for (AccountIdentity identity : aggregate.getIdentities()) {
            accountIdentityMapper.insert(AccountAssembler.toIdentityPO(identity, aggregate.getId()));
        }
    }
}

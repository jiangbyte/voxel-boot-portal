package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.AccountIdentityView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.AccountPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.AccountQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.AccountView;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import lombok.RequiredArgsConstructor;

/**
 * 账号只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountMapper accountMapper;
    private final AccountIdentityMapper accountIdentityMapper;


    @Override
    public AccountView detail(String id) {
        // 1. 按主键加载主表
        AccountPO po = accountMapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Account not found");
        }
        // 2. 一次查出全部身份，避免 N+1
        List<AccountIdentityPO> identities = accountIdentityMapper.selectList(
                Wrappers.<AccountIdentityPO>lambdaQuery().eq(AccountIdentityPO::getAccountId, id));
        // 3. 组装视图
        return toView(po, identities);
    }

    @Override
    public PageResult<AccountView> page(AccountPageQuery query) {
        // 1. 若按登录账号过滤，先批量查出命中的 accountId
        Set<String> accountIdsByLogin = null;
        if (StringUtils.hasText(query.account())) {
            List<AccountIdentityPO> hits = accountIdentityMapper.selectList(Wrappers.<AccountIdentityPO>lambdaQuery()
                    .eq(AccountIdentityPO::getIdentityType, "ACCOUNT")
                    .like(AccountIdentityPO::getIdentifier, query.account().trim())
                    .eq(AccountIdentityPO::getBindStatus, "BOUND"));
            accountIdsByLogin = hits.stream().map(AccountIdentityPO::getAccountId).collect(Collectors.toSet());
            if (accountIdsByLogin.isEmpty()) {
                return new PageResult<>(List.of(), 0, query.current(), query.size());
            }
        }
        // 2. 数据库分页查询主表
        Set<String> filterIds = accountIdsByLogin;
        Page<AccountPO> page = accountMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<AccountPO>lambdaQuery()
                        .in(filterIds != null, AccountPO::getId, filterIds)
                        .eq(StringUtils.hasText(query.accountType()), AccountPO::getAccountType, query.accountType())
                        .eq(StringUtils.hasText(query.accountStatus()), AccountPO::getAccountStatus, query.accountStatus())
                        .orderByDesc(AccountPO::getCreatedAt));
        List<AccountPO> records = page.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(List.of(), page.getTotal(), page.getCurrent(), page.getSize());
        }
        // 3. 批量加载本页身份，禁止循环内单条查询
        List<String> ids = records.stream().map(AccountPO::getId).toList();
        Map<String, List<AccountIdentityPO>> identityMap = loadIdentityMap(ids);
        // 4. 组装视图分页结果
        List<AccountView> views = records.stream()
                .map(po -> toView(po, identityMap.getOrDefault(po.getId(), List.of())))
                .toList();
        return new PageResult<>(views, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private Map<String, List<AccountIdentityPO>> loadIdentityMap(List<String> accountIds) {
        Map<String, List<AccountIdentityPO>> map = new HashMap<>();
        if (accountIds == null || accountIds.isEmpty()) {
            return map;
        }
        // IN 分批，单批 ≤ 500
        int batchSize = 500;
        for (int i = 0; i < accountIds.size(); i += batchSize) {
            List<String> batch = accountIds.subList(i, Math.min(i + batchSize, accountIds.size()));
            List<AccountIdentityPO> list = accountIdentityMapper.selectList(
                    Wrappers.<AccountIdentityPO>lambdaQuery().in(AccountIdentityPO::getAccountId, batch));
            for (AccountIdentityPO identity : list) {
                map.computeIfAbsent(identity.getAccountId(), key -> new ArrayList<>()).add(identity);
            }
        }
        return map;
    }

    private AccountView toView(AccountPO po, List<AccountIdentityPO> identities) {
        String primary = null;
        List<AccountIdentityView> identityViews = new ArrayList<>();
        if (identities != null) {
            for (AccountIdentityPO identity : identities) {
                if (primary == null
                        && "ACCOUNT".equalsIgnoreCase(identity.getIdentityType())
                        && java.util.Objects.equals(identity.getIsPrimary(), 1)) {
                    primary = identity.getIdentifier();
                }
                identityViews.add(new AccountIdentityView(
                        identity.getId(),
                        identity.getIdentityType(),
                        identity.getIdentifier(),
                        identity.getVerified(),
                        identity.getIsPrimary(),
                        identity.getBindStatus()));
            }
        }
        return new AccountView(
                po.getId(),
                po.getAccountType(),
                po.getAccountStatus(),
                primary,
                po.getLatestLoginIp(),
                po.getLatestLoginTime(),
                po.getLatestLoginDevice(),
                po.getCancelledAt(),
                po.getCancelledBy(),
                po.getCancelReason(),
                po.getCreatedAt(),
                po.getUpdatedAt(),
                identityViews);
    }
}

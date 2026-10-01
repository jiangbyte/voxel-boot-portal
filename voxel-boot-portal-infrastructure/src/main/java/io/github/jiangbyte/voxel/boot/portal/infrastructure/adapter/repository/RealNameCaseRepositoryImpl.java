package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCaseStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.RealNameCaseAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.RealNameCaseMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.RealNameCasePO;
import lombok.RequiredArgsConstructor;

/**
 * 实名工单仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class RealNameCaseRepositoryImpl implements RealNameCaseRepository {

    private final RealNameCaseMapper realNameCaseMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<RealNameCase> findById(String caseId) {
        if (!StringUtils.hasText(caseId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(RealNameCaseAssembler.toAggregate(realNameCaseMapper.selectById(caseId)));
    }

    @Override
    public void save(RealNameCase aggregate) {
        RealNameCasePO po = RealNameCaseAssembler.toPO(aggregate);
        RealNameCasePO existing = realNameCaseMapper.selectById(po.getCaseId());
        if (existing == null) {
            realNameCaseMapper.insert(po);
        } else {
            realNameCaseMapper.updateById(po);
        }
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String caseId) {
        if (!StringUtils.hasText(caseId)) {
            return;
        }
        realNameCaseMapper.deleteById(caseId);
    }

    @Override
    public long countPendingByAccount(String accountId, String businessType) {
        Long count = realNameCaseMapper.selectCount(Wrappers.<RealNameCasePO>lambdaQuery()
                .eq(RealNameCasePO::getAccountId, accountId)
                .eq(StringUtils.hasText(businessType), RealNameCasePO::getBusinessType, businessType)
                .eq(RealNameCasePO::getStatus, RealNameCaseStatus.PENDING.name()));
        return count == null ? 0L : count;
    }

    @Override
    public Optional<RealNameCase> findPendingByDocumentHash(String documentNoHash, String excludeAccountId) {
        if (!StringUtils.hasText(documentNoHash)) {
            return Optional.empty();
        }
        RealNameCasePO po = realNameCaseMapper.selectOne(Wrappers.<RealNameCasePO>lambdaQuery()
                .eq(RealNameCasePO::getDocumentNoHash, documentNoHash)
                .eq(RealNameCasePO::getStatus, RealNameCaseStatus.PENDING.name())
                .ne(StringUtils.hasText(excludeAccountId), RealNameCasePO::getAccountId, excludeAccountId)
                .last("LIMIT 1"));
        return Optional.ofNullable(RealNameCaseAssembler.toAggregate(po));
    }

    @Override
    public Optional<RealNameCase> findLatestPendingByAccount(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return Optional.empty();
        }
        RealNameCasePO po = realNameCaseMapper.selectOne(Wrappers.<RealNameCasePO>lambdaQuery()
                .eq(RealNameCasePO::getAccountId, accountId)
                .eq(RealNameCasePO::getStatus, RealNameCaseStatus.PENDING.name())
                .orderByDesc(RealNameCasePO::getCreatedAt)
                .last("LIMIT 1"));
        return Optional.ofNullable(RealNameCaseAssembler.toAggregate(po));
    }

    @Override
    public List<RealNameCase> pageByAccount(String accountId, String businessType, String status, long offset, long limit) {
        return realNameCaseMapper.selectList(Wrappers.<RealNameCasePO>lambdaQuery()
                        .eq(RealNameCasePO::getAccountId, accountId)
                        .eq(StringUtils.hasText(businessType), RealNameCasePO::getBusinessType, businessType)
                        .eq(StringUtils.hasText(status), RealNameCasePO::getStatus, status)
                        .orderByDesc(RealNameCasePO::getCreatedAt)
                        .last("LIMIT " + limit + " OFFSET " + offset))
                .stream()
                .map(RealNameCaseAssembler::toAggregate)
                .toList();
    }

    @Override
    public long countByAccount(String accountId, String businessType, String status) {
        Long count = realNameCaseMapper.selectCount(Wrappers.<RealNameCasePO>lambdaQuery()
                .eq(RealNameCasePO::getAccountId, accountId)
                .eq(StringUtils.hasText(businessType), RealNameCasePO::getBusinessType, businessType)
                .eq(StringUtils.hasText(status), RealNameCasePO::getStatus, status));
        return count == null ? 0L : count;
    }

    @Override
    public List<RealNameCase> pageForReview(String businessType, String status, String accountId, long offset, long limit) {
        return realNameCaseMapper.selectList(Wrappers.<RealNameCasePO>lambdaQuery()
                        .eq(RealNameCasePO::getBusinessType, businessType)
                        .eq(StringUtils.hasText(status), RealNameCasePO::getStatus, status)
                        .eq(StringUtils.hasText(accountId), RealNameCasePO::getAccountId, accountId)
                        .orderByDesc(RealNameCasePO::getCreatedAt)
                        .last("LIMIT " + limit + " OFFSET " + offset))
                .stream()
                .map(RealNameCaseAssembler::toAggregate)
                .toList();
    }

    @Override
    public long countForReview(String businessType, String status, String accountId) {
        Long count = realNameCaseMapper.selectCount(Wrappers.<RealNameCasePO>lambdaQuery()
                .eq(RealNameCasePO::getBusinessType, businessType)
                .eq(StringUtils.hasText(status), RealNameCasePO::getStatus, status)
                .eq(StringUtils.hasText(accountId), RealNameCasePO::getAccountId, accountId));
        return count == null ? 0L : count;
    }
}

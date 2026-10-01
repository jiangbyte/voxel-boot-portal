package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.IdentitySnapshotStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.ProfileIdentityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.ProfileIdentityAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.ProfileIdentityMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.identity.ProfileIdentityPO;
import lombok.RequiredArgsConstructor;

/**
 * 实名快照仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class ProfileIdentityRepositoryImpl implements ProfileIdentityRepository {

    private final ProfileIdentityMapper profileIdentityMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<ProfileIdentity> findById(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ProfileIdentityAssembler.toAggregate(profileIdentityMapper.selectById(accountId)));
    }

    @Override
    public void save(ProfileIdentity aggregate) {
        ProfileIdentityPO po = ProfileIdentityAssembler.toPO(aggregate);
        ProfileIdentityPO existing = profileIdentityMapper.selectById(po.getAccountId());
        if (existing == null) {
            profileIdentityMapper.insert(po);
        } else {
            profileIdentityMapper.updateById(po);
        }
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return;
        }
        profileIdentityMapper.deleteById(accountId);
    }

    @Override
    public Optional<ProfileIdentity> findVerifiedByDocumentHash(String documentNoHash, String excludeAccountId) {
        if (!StringUtils.hasText(documentNoHash)) {
            return Optional.empty();
        }
        ProfileIdentityPO po = profileIdentityMapper.selectOne(Wrappers.<ProfileIdentityPO>lambdaQuery()
                .eq(ProfileIdentityPO::getDocumentNoHash, documentNoHash)
                .eq(ProfileIdentityPO::getStatus, IdentitySnapshotStatus.VERIFIED.name())
                .ne(StringUtils.hasText(excludeAccountId), ProfileIdentityPO::getAccountId, excludeAccountId)
                .last("LIMIT 1"));
        return Optional.ofNullable(ProfileIdentityAssembler.toAggregate(po));
    }

    @Override
    public List<ProfileIdentity> page(String status, String accountId, String documentType, long offset, long limit) {
        return profileIdentityMapper.selectList(Wrappers.<ProfileIdentityPO>lambdaQuery()
                        .eq(StringUtils.hasText(status), ProfileIdentityPO::getStatus, status)
                        .eq(StringUtils.hasText(accountId), ProfileIdentityPO::getAccountId, accountId)
                        .eq(StringUtils.hasText(documentType), ProfileIdentityPO::getDocumentType, documentType)
                        .orderByDesc(ProfileIdentityPO::getVerifiedAt)
                        .last("LIMIT " + limit + " OFFSET " + offset))
                .stream()
                .map(ProfileIdentityAssembler::toAggregate)
                .toList();
    }

    @Override
    public long count(String status, String accountId, String documentType) {
        Long count = profileIdentityMapper.selectCount(Wrappers.<ProfileIdentityPO>lambdaQuery()
                .eq(StringUtils.hasText(status), ProfileIdentityPO::getStatus, status)
                .eq(StringUtils.hasText(accountId), ProfileIdentityPO::getAccountId, accountId)
                .eq(StringUtils.hasText(documentType), ProfileIdentityPO::getDocumentType, documentType));
        return count == null ? 0L : count;
    }

    @Override
    public List<ProfileIdentity> findByIds(Collection<String> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) {
            return List.of();
        }
        List<String> ids = accountIds.stream().filter(StringUtils::hasText).distinct().toList();
        if (ids.isEmpty()) {
            return List.of();
        }
        return profileIdentityMapper.selectByIds(ids).stream()
                .map(ProfileIdentityAssembler::toAggregate)
                .toList();
    }

    @Override
    public List<ProfileIdentity> listVerified() {
        return profileIdentityMapper.selectList(Wrappers.<ProfileIdentityPO>lambdaQuery()
                        .eq(ProfileIdentityPO::getStatus, IdentitySnapshotStatus.VERIFIED.name())
                        .isNotNull(ProfileIdentityPO::getRealNameCipher))
                .stream()
                .map(ProfileIdentityAssembler::toAggregate)
                .toList();
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.AdminProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.adapter.repository.AdminProfileRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.admin.AdminProfileAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.admin.AdminProfileMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.admin.AdminProfilePO;
import lombok.RequiredArgsConstructor;

/**
 * 管理端资料仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class AdminProfileRepositoryImpl implements AdminProfileRepository {

    private final AdminProfileMapper adminProfileMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<AdminProfile> findById(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(AdminProfileAssembler.toAggregate(adminProfileMapper.selectById(accountId)));
    }

    @Override
    public void save(AdminProfile aggregate) {
        // 1. 聚合转 PO
        AdminProfilePO po = AdminProfileAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        AdminProfilePO existing = adminProfileMapper.selectById(po.getAccountId());
        if (existing == null) {
            adminProfileMapper.insert(po);
        } else {
            adminProfileMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return;
        }
        adminProfileMapper.deleteById(accountId);
    }
}

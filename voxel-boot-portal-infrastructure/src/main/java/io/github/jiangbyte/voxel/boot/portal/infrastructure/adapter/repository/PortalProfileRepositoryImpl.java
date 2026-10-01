package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.PortalProfile;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.adapter.repository.PortalProfileRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.portal.PortalProfileAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.portal.PortalProfileMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.portal.PortalProfilePO;
import lombok.RequiredArgsConstructor;

/**
 * 门户资料仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class PortalProfileRepositoryImpl implements PortalProfileRepository {

    private final PortalProfileMapper portalProfileMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<PortalProfile> findById(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(PortalProfileAssembler.toAggregate(portalProfileMapper.selectById(accountId)));
    }

    @Override
    public void save(PortalProfile aggregate) {
        PortalProfilePO po = PortalProfileAssembler.INSTANCE.toPO(aggregate);
        PortalProfilePO existing = portalProfileMapper.selectById(po.getAccountId());
        if (existing == null) {
            portalProfileMapper.insert(po);
        } else {
            portalProfileMapper.updateById(po);
        }
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return;
        }
        portalProfileMapper.deleteById(accountId);
    }
}

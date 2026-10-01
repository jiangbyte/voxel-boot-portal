package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.repository.BannerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner.BannerAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner.BannerMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner.BannerPO;
import lombok.RequiredArgsConstructor;

/**
 * Banner 仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepository {

    private final BannerMapper bannerMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Banner> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(BannerAssembler.toAggregate(bannerMapper.selectById(id)));
    }

    @Override
    public void save(Banner aggregate) {
        // 1. 聚合转 PO
        BannerPO po = BannerAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        BannerPO existing = bannerMapper.selectById(po.getId());
        if (existing == null) {
            bannerMapper.insert(po);
        } else {
            bannerMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        bannerMapper.deleteById(id);
    }

    @Override
    public void remove(Banner aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        bannerMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.Org;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.repository.OrgRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.org.OrgAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.org.OrgMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.org.OrgPO;
import lombok.RequiredArgsConstructor;

/**
 * 组织仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class OrgRepositoryImpl implements OrgRepository {

    private final OrgMapper orgMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Org> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(OrgAssembler.toAggregate(orgMapper.selectById(id)));
    }

    @Override
    public boolean existsByParentId(String parentId) {
        if (!StringUtils.hasText(parentId)) {
            return false;
        }
        return orgMapper.selectCount(Wrappers.<OrgPO>lambdaQuery()
                .eq(OrgPO::getParentId, parentId)) > 0;
    }

    @Override
    public void save(Org aggregate) {
        // 1. 聚合转 PO
        OrgPO po = OrgAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        OrgPO existing = orgMapper.selectById(po.getId());
        if (existing == null) {
            orgMapper.insert(po);
        } else {
            orgMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        orgMapper.deleteById(id);
    }

    @Override
    public void remove(Org aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        orgMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

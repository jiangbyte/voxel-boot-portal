package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.Position;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.repository.PositionRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.position.PositionAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.position.PositionMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.position.PositionPO;
import lombok.RequiredArgsConstructor;

/**
 * 岗位仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepository {

    private final PositionMapper positionMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Position> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(PositionAssembler.toAggregate(positionMapper.selectById(id)));
    }

    @Override
    public void save(Position aggregate) {
        // 1. 聚合转 PO
        PositionPO po = PositionAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        PositionPO existing = positionMapper.selectById(po.getId());
        if (existing == null) {
            positionMapper.insert(po);
        } else {
            positionMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        positionMapper.deleteById(id);
    }

    @Override
    public void remove(Position aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        positionMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.repository.GroupRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.group.GroupAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.group.GroupMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.group.GroupPO;
import lombok.RequiredArgsConstructor;

/**
 * 用户组仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final GroupMapper groupMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Group> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(GroupAssembler.toAggregate(groupMapper.selectById(id)));
    }

    @Override
    public Optional<Group> findByName(String name) {
        if (!StringUtils.hasText(name)) {
            return Optional.empty();
        }
        GroupPO po = groupMapper.selectOne(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getName, name)
                .last("LIMIT 1"));
        return Optional.ofNullable(GroupAssembler.toAggregate(po));
    }

    @Override
    public void save(Group aggregate) {
        // 1. 聚合转 PO
        GroupPO po = GroupAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        GroupPO existing = groupMapper.selectById(po.getId());
        if (existing == null) {
            groupMapper.insert(po);
        } else {
            groupMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        groupMapper.deleteById(id);
    }

    @Override
    public void remove(Group aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        groupMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

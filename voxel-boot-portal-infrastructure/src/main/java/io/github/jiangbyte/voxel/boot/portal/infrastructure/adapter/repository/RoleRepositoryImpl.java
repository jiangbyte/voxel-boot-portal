package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.repository.RoleRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role.RoleAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role.RoleMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role.RolePO;
import lombok.RequiredArgsConstructor;

/**
 * 角色仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Role> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(RoleAssembler.toAggregate(roleMapper.selectById(id)));
    }

    @Override
    public Optional<Role> findByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return Optional.empty();
        }
        RolePO po = roleMapper.selectOne(Wrappers.<RolePO>lambdaQuery()
                .eq(RolePO::getCode, code)
                .last("LIMIT 1"));
        return Optional.ofNullable(RoleAssembler.toAggregate(po));
    }

    @Override
    public void save(Role aggregate) {
        // 1. 聚合转 PO
        RolePO po = RoleAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        RolePO existing = roleMapper.selectById(po.getId());
        if (existing == null) {
            roleMapper.insert(po);
        } else {
            roleMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        roleMapper.deleteById(id);
    }

    @Override
    public void remove(Role aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        roleMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

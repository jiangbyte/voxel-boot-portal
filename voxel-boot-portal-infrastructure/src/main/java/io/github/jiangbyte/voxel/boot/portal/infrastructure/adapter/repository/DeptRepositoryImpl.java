package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.Dept;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.repository.DeptRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.dept.DeptAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.dept.DeptMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.dept.DeptPO;
import lombok.RequiredArgsConstructor;

/**
 * 部门仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class DeptRepositoryImpl implements DeptRepository {

    private final DeptMapper deptMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Dept> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(DeptAssembler.toAggregate(deptMapper.selectById(id)));
    }

    @Override
    public boolean existsByParentId(String parentId) {
        if (!StringUtils.hasText(parentId)) {
            return false;
        }
        return deptMapper.selectCount(Wrappers.<DeptPO>lambdaQuery()
                .eq(DeptPO::getParentId, parentId)) > 0;
    }

    @Override
    public void save(Dept aggregate) {
        // 1. 聚合转 PO
        DeptPO po = DeptAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        DeptPO existing = deptMapper.selectById(po.getId());
        if (existing == null) {
            deptMapper.insert(po);
        } else {
            deptMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        deptMapper.deleteById(id);
    }

    @Override
    public void remove(Dept aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        deptMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

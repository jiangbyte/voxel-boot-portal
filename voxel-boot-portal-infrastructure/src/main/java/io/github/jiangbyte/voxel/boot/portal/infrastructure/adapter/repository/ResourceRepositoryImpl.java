package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.repository.ResourceRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourcePO;
import lombok.RequiredArgsConstructor;

/**
 * 资源仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class ResourceRepositoryImpl implements ResourceRepository {

    private final ResourceMapper resourceMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Resource> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ResourceAssembler.toAggregate(resourceMapper.selectById(id)));
    }

    @Override
    public Optional<Resource> findByModuleIdAndCode(String moduleId, String code) {
        if (!StringUtils.hasText(code)) {
            return Optional.empty();
        }
        ResourcePO po = resourceMapper.selectOne(Wrappers.<ResourcePO>lambdaQuery()
                .eq(StringUtils.hasText(moduleId), ResourcePO::getModuleId, moduleId)
                .isNull(!StringUtils.hasText(moduleId), ResourcePO::getModuleId)
                .eq(ResourcePO::getCode, code)
                .last("LIMIT 1"));
        return Optional.ofNullable(ResourceAssembler.toAggregate(po));
    }

    @Override
    public boolean existsByParentId(String parentId) {
        if (!StringUtils.hasText(parentId)) {
            return false;
        }
        return resourceMapper.selectCount(Wrappers.<ResourcePO>lambdaQuery()
                .eq(ResourcePO::getParentId, parentId)) > 0;
    }

    @Override
    public void save(Resource aggregate) {
        // 1. 聚合转 PO
        ResourcePO po = ResourceAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        ResourcePO existing = resourceMapper.selectById(po.getId());
        if (existing == null) {
            resourceMapper.insert(po);
        } else {
            resourceMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        resourceMapper.deleteById(id);
    }

    @Override
    public void remove(Resource aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        resourceMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

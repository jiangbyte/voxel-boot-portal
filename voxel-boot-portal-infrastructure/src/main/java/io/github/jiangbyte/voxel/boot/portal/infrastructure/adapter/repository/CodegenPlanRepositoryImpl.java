package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository.CodegenPlanRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen.CodegenPlanAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen.CodegenPlanMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen.CodegenPlanPO;
import lombok.RequiredArgsConstructor;

/**
 * 代码生成方案仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class CodegenPlanRepositoryImpl implements CodegenPlanRepository {

    private final CodegenPlanMapper mapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public void save(CodegenPlan plan) {
        CodegenPlanPO po = CodegenPlanAssembler.INSTANCE.toPO(plan);
        CodegenPlanPO existing = mapper.selectById(po.getId());
        if (existing == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        domainEventPublisher.publishAll(plan.pullDomainEvents());
    }

    @Override
    public void remove(CodegenPlan plan) {
        if (plan == null || !StringUtils.hasText(plan.getId())) {
            return;
        }
        mapper.deleteById(plan.getId());
        domainEventPublisher.publishAll(plan.pullDomainEvents());
    }

    @Override
    public Optional<CodegenPlan> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(CodegenPlanAssembler.toAggregate(mapper.selectById(id)));
    }

    @Override
    public List<CodegenPlan> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return mapper.selectByIds(ids).stream().map(CodegenPlanAssembler::toAggregate).toList();
    }

    @Override
    public boolean existsByName(String name, String excludeId) {
        Long count = mapper.selectCount(Wrappers.<CodegenPlanPO>lambdaQuery()
                .eq(CodegenPlanPO::getName, name)
                .ne(StringUtils.hasText(excludeId), CodegenPlanPO::getId, excludeId));
        return count != null && count > 0;
    }
}

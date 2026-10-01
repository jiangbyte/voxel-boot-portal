package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository.CodegenFieldRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen.CodegenFieldAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen.CodegenFieldMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.codegen.CodegenFieldPO;
import lombok.RequiredArgsConstructor;

/**
 * 代码生成字段仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class CodegenFieldRepositoryImpl implements CodegenFieldRepository {

    private final CodegenFieldMapper mapper;


    @Override
    public List<CodegenField> listByPlanId(String planId, String tableRole) {
        if (!StringUtils.hasText(planId)) {
            return Collections.emptyList();
        }
        return mapper.selectList(Wrappers.<CodegenFieldPO>lambdaQuery()
                        .eq(CodegenFieldPO::getPlanId, planId)
                        .eq(StringUtils.hasText(tableRole), CodegenFieldPO::getTableRole, tableRole)
                        .orderByAsc(CodegenFieldPO::getTableRole)
                        .orderByAsc(CodegenFieldPO::getSort)
                        .orderByAsc(CodegenFieldPO::getId))
                .stream()
                .map(CodegenFieldAssembler::toDomain)
                .toList();
    }

    @Override
    public void replaceAll(String planId, List<CodegenField> fields) {
        // 1. 删除方案下全部字段
        mapper.delete(Wrappers.<CodegenFieldPO>lambdaQuery().eq(CodegenFieldPO::getPlanId, planId));
        // 2. 逐条插入新配置
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (CodegenField field : fields) {
            mapper.insert(CodegenFieldAssembler.INSTANCE.toPO(field));
        }
    }

    @Override
    public void deleteByPlanIds(List<String> planIds) {
        if (planIds == null || planIds.isEmpty()) {
            return;
        }
        mapper.delete(Wrappers.<CodegenFieldPO>lambdaQuery().in(CodegenFieldPO::getPlanId, planIds));
    }
}

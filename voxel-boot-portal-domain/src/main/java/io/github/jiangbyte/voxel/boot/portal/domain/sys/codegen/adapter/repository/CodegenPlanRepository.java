package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository;

import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenPlan;

/**
 * 代码生成方案仓储端口。
 *
 * Author: Charlie
 */
public interface CodegenPlanRepository {

    void save(CodegenPlan plan);

    void remove(CodegenPlan plan);

    Optional<CodegenPlan> findById(String id);

    List<CodegenPlan> findByIds(List<String> ids);

    boolean existsByName(String name, String excludeId);
}

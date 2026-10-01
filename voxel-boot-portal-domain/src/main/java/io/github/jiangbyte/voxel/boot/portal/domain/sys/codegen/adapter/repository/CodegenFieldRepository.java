package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.repository;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.CodegenField;

/**
 * 代码生成字段仓储端口。
 *
 * Author: Charlie
 */
public interface CodegenFieldRepository {

    List<CodegenField> listByPlanId(String planId, String tableRole);

    void replaceAll(String planId, List<CodegenField> fields);

    void deleteByPlanIds(List<String> planIds);
}

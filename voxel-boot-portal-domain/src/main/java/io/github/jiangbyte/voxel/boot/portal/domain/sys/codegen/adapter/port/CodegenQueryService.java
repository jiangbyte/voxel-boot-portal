package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 代码生成查询端口。
 *
 * Author: Charlie
 */
public interface CodegenQueryService {

    CodegenPlanView detail(String id);

    PageResult<CodegenPlanView> page(CodegenPlanPageQuery query);

    List<CodegenDatabaseTableView> tables();

    List<CodegenDatabaseColumnView> tableColumns(String tableName);

    List<CodegenFieldView> fields(String planId, String tableRole);

    List<Tree<String>> parentResources(String moduleId);

    CodegenPreviewView preview(String id);

    byte[] download(String id);
}

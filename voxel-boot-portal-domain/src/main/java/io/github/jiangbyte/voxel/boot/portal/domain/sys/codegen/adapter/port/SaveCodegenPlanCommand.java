package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

/**
 * 创建/更新方案命令（id 为空表示创建）。
 *
 * Author: Charlie
 */
public record SaveCodegenPlanCommand(
        String id,
        String name,
        String genType,
        String author,
        String description,
        String tableName,
        String pkColumn,
        String entityName,
        String modulePath,
        String businessName,
        String apiPrefix,
        String permissionPrefix,
        String resourceModuleId,
        String parentResourceId,
        String menuName,
        String menuPath,
        String componentPath,
        String icon,
        Integer sort,
        String treeParentField,
        String treeLabelField,
        String subTable,
        String subPk,
        String subForeignKey,
        String subEntityName,
        String subBusinessName
) {
}

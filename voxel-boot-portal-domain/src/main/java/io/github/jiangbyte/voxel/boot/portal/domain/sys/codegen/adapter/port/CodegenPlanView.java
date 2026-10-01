package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

import java.time.LocalDateTime;

/**
 * 代码生成方案只读视图。
 *
 * Author: Charlie
 */
public record CodegenPlanView(
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
        String subBusinessName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

/**
 * 代码生成字段只读视图。
 *
 * Author: Charlie
 */
public record CodegenFieldView(
        String id,
        String planId,
        String tableRole,
        String columnName,
        String label,
        String dbType,
        String valueType,
        String uiType,
        String widget,
        String dictCode,
        String queryOperator,
        Integer inTable,
        Integer inForm,
        Integer inDetail,
        Integer inQuery,
        Integer primaryKey,
        Integer required,
        Integer uniqueFlag,
        Integer nullable,
        Integer maxLength,
        Integer sort
) {
}

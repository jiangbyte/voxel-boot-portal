package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

/**
 * 数据库列元数据视图。
 *
 * Author: Charlie
 */
public record CodegenDatabaseColumnView(
        String columnName,
        String label,
        String dbType,
        String valueType,
        String uiType,
        Integer primaryKey,
        Integer nullable,
        Integer maxLength
) {
}

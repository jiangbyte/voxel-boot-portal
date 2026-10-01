package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port;

import java.util.List;

/**
 * 数据库 schema 内省端口（JDBC DatabaseMetaData）。
 *
 * Author: Charlie
 */
public interface CodegenSchemaPort {

    List<CodegenDatabaseTableView> listTables();

    List<CodegenDatabaseColumnView> listColumns(String tableName);
}

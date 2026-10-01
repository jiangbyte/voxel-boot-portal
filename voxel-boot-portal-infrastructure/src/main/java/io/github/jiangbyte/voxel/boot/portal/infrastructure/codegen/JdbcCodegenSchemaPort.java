package io.github.jiangbyte.voxel.boot.portal.infrastructure.codegen;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenDatabaseColumnView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenDatabaseTableView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenSchemaPort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * 基于 JDBC DatabaseMetaData 的 schema 内省。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class JdbcCodegenSchemaPort implements CodegenSchemaPort {

    private final DataSource dataSource;


    @Override
    public List<CodegenDatabaseTableView> listTables() {
        // 1. 打开连接读取元数据
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = resolveSchema(connection);
            List<CodegenDatabaseTableView> tables = new ArrayList<>();
            // 2. 枚举 TABLE
            try (ResultSet rs = meta.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    if (!StringUtils.hasText(name) || name.toLowerCase(Locale.ROOT).startsWith("flyway_")) {
                        continue;
                    }
                    String remark = rs.getString("REMARKS");
                    tables.add(new CodegenDatabaseTableView(name, blankToNull(remark)));
                }
            }
            return tables;
        } catch (SQLException ex) {
            throw new DomainException("Failed to list tables: " + ex.getMessage());
        }
    }

    @Override
    public List<CodegenDatabaseColumnView> listColumns(String tableName) {
        // 1. 校验表名
        requireIdent(tableName);
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = resolveSchema(connection);
            // 2. 主键集合
            Set<String> pks = new HashSet<>();
            try (ResultSet pkRs = meta.getPrimaryKeys(catalog, schema, tableName)) {
                while (pkRs.next()) {
                    pks.add(pkRs.getString("COLUMN_NAME"));
                }
            }
            // 3. 列明细
            List<CodegenDatabaseColumnView> columns = new ArrayList<>();
            try (ResultSet rs = meta.getColumns(catalog, schema, tableName, "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String typeName = rs.getString("TYPE_NAME");
                    String dataType = rs.getString("TYPE_NAME");
                    String remarks = rs.getString("REMARKS");
                    String nullable = rs.getString("IS_NULLABLE");
                    Integer maxLength = null;
                    int size = rs.getInt("COLUMN_SIZE");
                    if (!rs.wasNull()) {
                        maxLength = size;
                    }
                    String[] types = DbTypeMapper.toDataAndFrontendType(dataType, typeName);
                    columns.add(new CodegenDatabaseColumnView(
                            columnName,
                            blankToNull(remarks),
                            typeName,
                            types[0],
                            types[1],
                            Flags.of(pks.contains(columnName)),
                            Flags.of("YES".equalsIgnoreCase(nullable)),
                            maxLength));
                }
            }
            // 4. 空则表不存在
            if (columns.isEmpty()) {
                throw new DomainException(404, "Database table not found");
            }
            return columns;
        } catch (DomainException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new DomainException("Failed to list columns: " + ex.getMessage());
        }
    }

    private static String resolveSchema(Connection connection) throws SQLException {
        try {
            return connection.getSchema();
        } catch (AbstractMethodError | SQLException ignored) {
            return null;
        }
    }

    private static void requireIdent(String tableName) {
        if (!StringUtils.hasText(tableName) || !tableName.matches("[A-Za-z0-9_]+")) {
            throw new DomainException("Invalid table name");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

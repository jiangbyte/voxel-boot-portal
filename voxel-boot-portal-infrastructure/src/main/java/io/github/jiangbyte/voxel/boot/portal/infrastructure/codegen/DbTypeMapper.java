package io.github.jiangbyte.voxel.boot.portal.infrastructure.codegen;

/**
 * 数据库类型到语义/前端类型映射。
 *
 * Author: Charlie
 */
public final class DbTypeMapper {

    private DbTypeMapper() {
    }

    /**
     * @return [valueType, uiType]
     */
    public static String[] toDataAndFrontendType(String dataType, String typeName) {
        String raw = (typeName == null || typeName.isBlank() ? dataType : typeName).toLowerCase().trim();
        String type = raw;
        int paren = raw.indexOf('(');
        String base = paren > 0 ? raw.substring(0, paren) : raw;
        String args = paren > 0 ? raw.substring(paren) : "";

        if ("tinyint".equals(base) && args.startsWith("(1)")) {
            return new String[]{"bool", "boolean"};
        }

        return switch (base) {
            case "int2", "int4", "int8", "integer", "bigint", "smallint", "serial", "bigserial",
                 "int", "mediumint", "tinyint" -> new String[]{"int", "number"};
            case "numeric", "decimal", "float4", "float8", "double precision", "real", "money",
                 "double", "float" -> new String[]{"float", "number"};
            case "bool", "boolean" -> new String[]{"bool", "boolean"};
            case "json", "jsonb" -> new String[]{"dict", "Record<string, any>"};
            case "timestamp", "timestamptz", "timestamp without time zone", "timestamp with time zone",
                 "date", "time", "timetz", "datetime" -> new String[]{"datetime", "string"};
            default -> {
                if (type.contains("timestamp") || type.equals("datetime") || type.equals("date") || type.equals("time")) {
                    yield new String[]{"datetime", "string"};
                }
                if (type.equals("json") || type.equals("jsonb")) {
                    yield new String[]{"dict", "Record<string, any>"};
                }
                yield new String[]{"str", "string"};
            }
        };
    }

    public static String toJavaType(String valueType) {
        if (valueType == null) {
            return "String";
        }
        return switch (valueType) {
            case "int" -> "Integer";
            case "float" -> "java.math.BigDecimal";
            case "bool" -> "Boolean";
            case "datetime" -> "java.time.LocalDateTime";
            case "dict" -> "java.util.Map<String, Object>";
            default -> "String";
        };
    }
}

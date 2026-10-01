package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen;

import java.util.UUID;

/**
 * 代码生成字段配置（方案内值对象级实体）。
 *
 * Author: Charlie
 */
public class CodegenField {

    private String id;
    private String planId;
    private String tableRole;
    private String columnName;
    private String label;
    private String dbType;
    private String valueType;
    private String uiType;
    private String widget;
    private String dictCode;
    private String queryOperator;
    private Integer inTable;
    private Integer inForm;
    private Integer inDetail;
    private Integer inQuery;
    private Integer primaryKey;
    private Integer required;
    private Integer uniqueFlag;
    private Integer nullable;
    private Integer maxLength;
    private Integer sort;

    private CodegenField() {
    }

    public static CodegenField create(String planId, String tableRole, String columnName, String label,
                                      String dbType, String valueType, String uiType, String widget,
                                      String dictCode, String queryOperator, Integer inTable, Integer inForm,
                                      Integer inDetail, Integer inQuery, Integer primaryKey, Integer required,
                                      Integer uniqueFlag, Integer nullable, Integer maxLength, Integer sort) {
        CodegenField field = new CodegenField();
        field.id = UUID.randomUUID().toString().replace("-", "");
        field.planId = planId;
        field.tableRole = tableRole;
        field.columnName = columnName;
        field.label = label;
        field.dbType = dbType;
        field.valueType = valueType;
        field.uiType = uiType;
        field.widget = widget;
        field.dictCode = dictCode;
        field.queryOperator = queryOperator;
        field.inTable = inTable;
        field.inForm = inForm;
        field.inDetail = inDetail;
        field.inQuery = inQuery;
        field.primaryKey = primaryKey;
        field.required = required;
        field.uniqueFlag = uniqueFlag;
        field.nullable = nullable;
        field.maxLength = maxLength;
        field.sort = sort == null ? 0 : sort;
        return field;
    }

    public static CodegenField rehydrate(String id, String planId, String tableRole, String columnName, String label,
                                         String dbType, String valueType, String uiType, String widget,
                                         String dictCode, String queryOperator, Integer inTable, Integer inForm,
                                         Integer inDetail, Integer inQuery, Integer primaryKey, Integer required,
                                         Integer uniqueFlag, Integer nullable, Integer maxLength, Integer sort) {
        CodegenField field = create(planId, tableRole, columnName, label, dbType, valueType, uiType, widget,
                dictCode, queryOperator, inTable, inForm, inDetail, inQuery, primaryKey, required,
                uniqueFlag, nullable, maxLength, sort);
        field.id = id;
        return field;
    }

    public String getId() { return id; }
    public String getPlanId() { return planId; }
    public String getTableRole() { return tableRole; }
    public String getColumnName() { return columnName; }
    public String getLabel() { return label; }
    public String getDbType() { return dbType; }
    public String getValueType() { return valueType; }
    public String getUiType() { return uiType; }
    public String getWidget() { return widget; }
    public String getDictCode() { return dictCode; }
    public String getQueryOperator() { return queryOperator; }
    public Integer getInTable() { return inTable; }
    public Integer getInForm() { return inForm; }
    public Integer getInDetail() { return inDetail; }
    public Integer getInQuery() { return inQuery; }
    public Integer getPrimaryKey() { return primaryKey; }
    public Integer getRequired() { return required; }
    public Integer getUniqueFlag() { return uniqueFlag; }
    public Integer getNullable() { return nullable; }
    public Integer getMaxLength() { return maxLength; }
    public Integer getSort() { return sort; }
}

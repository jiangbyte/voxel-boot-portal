package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代码生成字段响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "代码生成字段响应")
public class SysCodegenFieldResult {
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
}

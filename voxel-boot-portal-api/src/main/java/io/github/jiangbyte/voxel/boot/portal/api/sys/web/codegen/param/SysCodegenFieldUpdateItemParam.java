package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段批量更新单项。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "字段批量更新单项")
public class SysCodegenFieldUpdateItemParam {
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

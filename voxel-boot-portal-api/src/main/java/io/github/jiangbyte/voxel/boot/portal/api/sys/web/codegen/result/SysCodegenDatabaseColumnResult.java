package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据库列元数据响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "数据库列元数据响应")
public class SysCodegenDatabaseColumnResult {
    private String columnName;
    private String label;
    private String dbType;
    private String valueType;
    private String uiType;
    private Integer primaryKey;
    private Integer nullable;
    private Integer maxLength;
}

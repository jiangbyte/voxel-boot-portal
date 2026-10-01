package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据库表元数据响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "数据库表元数据响应")
public class SysCodegenDatabaseTableResult {
    private String tableName;
    private String tableComment;
}

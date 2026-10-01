package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成方案分页入参。
 *
 * Author: Charlie
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "代码生成方案分页入参")
public class SysCodegenPlanPageParam extends PageQuery {
    private String name;
    private String tableName;
    private String genType;
}

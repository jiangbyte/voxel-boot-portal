package io.github.jiangbyte.voxel.boot.portal.api.iam.web.resource.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "资源分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysResourcePageParam extends PageQuery {

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "资源类型")
    private String resourceType;

    @Schema(description = "模块ID")
    private String moduleId;

    @Schema(description = "状态")
    private String status;
}

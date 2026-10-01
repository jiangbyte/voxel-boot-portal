package io.github.jiangbyte.voxel.boot.portal.api.iam.web.dept.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "部门分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptPageParam extends PageQuery {

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门类别")
    private String category;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "父级部门ID")
    private String parentId;
}

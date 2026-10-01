package io.github.jiangbyte.voxel.boot.portal.api.iam.web.org.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "组织分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOrgPageParam extends PageQuery {

    @Schema(description = "组织名称")
    private String name;

    @Schema(description = "组织类别")
    private String category;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "父级组织ID")
    private String parentId;
}

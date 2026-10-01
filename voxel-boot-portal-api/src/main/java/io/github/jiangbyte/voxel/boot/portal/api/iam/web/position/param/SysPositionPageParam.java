package io.github.jiangbyte.voxel.boot.portal.api.iam.web.position.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "岗位分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPositionPageParam extends PageQuery {

    @Schema(description = "职位名称")
    private String name;

    @Schema(description = "职位类别")
    private String category;

    @Schema(description = "职位状态：ENABLED/DISABLED")
    private String status;
}

package io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户组分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "用户组分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysGroupPageParam extends PageQuery {

    @Schema(description = "用户组名称")
    private String name;

    @Schema(description = "所属部门ID")
    private String ownerDeptId;

    @Schema(description = "状态")
    private String status;
}

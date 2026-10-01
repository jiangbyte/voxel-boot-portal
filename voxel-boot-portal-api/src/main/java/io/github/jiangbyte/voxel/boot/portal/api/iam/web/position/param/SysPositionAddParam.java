package io.github.jiangbyte.voxel.boot.portal.api.iam.web.position.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建岗位入参。
 *
 * Author: Charlie
 */
@Schema(description = "创建岗位入参")
@Data
public class SysPositionAddParam {

    @NotBlank
    @Schema(description = "职位名称")
    private String name;

    @NotBlank
    @Schema(description = "职位类别")
    private String category;

    @Schema(description = "所属部门ID（数据权限范围）")
    private String ownerDeptId;

    @Schema(description = "排序号（越小越靠前）")
    private Integer sort = 99;

    @Schema(description = "是否虚拟组织：1 虚拟 / 0 实体")
    private Integer isVirtual = 0;

    @Schema(description = "职位状态：ENABLED/DISABLED")
    private String status = "ENABLED";

    @Schema(description = "职位描述")
    private String description;

    @Schema(description = "扩展信息 JSON")
    private String extra;
}

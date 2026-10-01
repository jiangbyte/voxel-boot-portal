package io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 编辑用户组入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑用户组入参")
@Data
public class SysGroupEditParam {

    @NotBlank
    @Schema(description = "主键ID")
    private String id;

    @NotBlank
    @Schema(description = "用户组名称")
    private String name;

    @Schema(description = "所属部门ID")
    private String ownerDeptId;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：ENABLED/DISABLED")
    private String status = "ENABLED";

    @Schema(description = "扩展信息 JSON")
    private String extra;
}

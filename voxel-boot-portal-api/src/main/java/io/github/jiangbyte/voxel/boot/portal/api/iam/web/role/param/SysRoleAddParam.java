package io.github.jiangbyte.voxel.boot.portal.api.iam.web.role.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建角色入参。
 *
 * Author: Charlie
 */
@Schema(description = "创建角色入参")
@Data
public class SysRoleAddParam {

    @NotBlank
    @Schema(description = "编码")
    private String code;

    @NotBlank
    @Schema(description = "名称")
    private String name;

    @Schema(description = "角色分类")
    private String category;

    @Schema(description = "角色作用域：GLOBAL/DEPT")
    private String scopeType;

    @Schema(description = "所属部门ID")
    private String ownerDeptId;

    @Schema(description = "排序号")
    private Integer sort = 99;

    @Schema(description = "角色状态：ENABLED/DISABLED")
    private String status = "ENABLED";

    @Schema(description = "是否内置")
    private Integer isBuiltin = 0;

    @Schema(description = "角色描述")
    private String description;
}

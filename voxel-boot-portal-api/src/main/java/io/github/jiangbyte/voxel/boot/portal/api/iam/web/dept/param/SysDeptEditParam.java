package io.github.jiangbyte.voxel.boot.portal.api.iam.web.dept.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 编辑部门入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑部门入参")
@Data
public class SysDeptEditParam {

    @NotBlank
    @Schema(description = "主键ID")
    private String id;

    @NotBlank
    @Schema(description = "部门名称")
    private String name;

    @NotBlank
    @Schema(description = "部门类别")
    private String category;

    @Schema(description = "父级部门ID")
    private String parentId;

    @Schema(description = "主管账户ID")
    private String masterId;

    @Schema(description = "副主管账户ID")
    private String deputyMasterId;

    @Schema(description = "排序号")
    private Integer sort = 99;

    @Schema(description = "是否虚拟组织")
    private Integer isVirtual = 0;

    @Schema(description = "状态：ENABLED/DISABLED")
    private String status = "ENABLED";

    @Schema(description = "扩展信息 JSON")
    private String extra;
}

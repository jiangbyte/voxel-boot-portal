package io.github.jiangbyte.voxel.boot.portal.api.iam.web.org.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建组织入参。
 *
 * Author: Charlie
 */
@Schema(description = "创建组织入参")
@Data
public class SysOrgAddParam {

    @NotBlank
    @Schema(description = "组织名称")
    private String name;

    @NotBlank
    @Schema(description = "组织类别")
    private String category;

    @Schema(description = "父级组织ID")
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

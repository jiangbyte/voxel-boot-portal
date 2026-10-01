package io.github.jiangbyte.voxel.boot.portal.api.sys.web.dict.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑数据字典入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑数据字典入参")
@Data
public class SysDictEditParam {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "主键ID")
    private String id;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9_]+$")
    @Schema(description = "字典项编码（同父级下唯一）")
    private String code;

    @Schema(description = "字典项展示标签")
    private String label;

    @Schema(description = "字典项实际值")
    private String value;

    @Schema(description = "前端展示颜色")
    private String color;

    @Schema(description = "字典分类：SYS / BIZ")
    private String category;

    @Schema(description = "父级字典项ID")
    private String parentId;

    @Schema(description = "字典项状态：ENABLED/DISABLED")
    private String status = "ENABLED";

    @Schema(description = "排序号（越小越靠前）")
    private Integer sort = 0;
}

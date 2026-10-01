package io.github.jiangbyte.voxel.boot.portal.api.sys.web.config.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 批量保存配置单项入参。
 *
 * Author: Charlie
 */
@Schema(description = "批量保存配置单项入参")
@Data
public class SysConfigBatchItemParam {

    @NotBlank
    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "值类型")
    private String valueType;

    @Schema(description = "展示名称")
    private String label;

    @Schema(description = "排序码")
    private Integer sortCode;

    @Schema(description = "乐观锁版本（可选；提交时校验冲突）")
    private Integer version;
}

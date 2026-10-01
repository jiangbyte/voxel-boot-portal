package io.github.jiangbyte.voxel.boot.portal.api.sys.web.config.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量保存系统配置入参。
 *
 * Author: Charlie
 */
@Schema(description = "批量保存系统配置入参")
@Data
public class SysConfigBatchSaveParam {

    @NotEmpty
    @Valid
    @Schema(description = "配置项列表")
    private List<SysConfigBatchItemParam> items;
}

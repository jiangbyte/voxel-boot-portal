package io.github.jiangbyte.voxel.boot.portal.api.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量 ID 入参。
 */
@Data
public class IdsParam {
    @NotEmpty
    @Schema(description = "主键ID列表")
    private List<String> ids;
}

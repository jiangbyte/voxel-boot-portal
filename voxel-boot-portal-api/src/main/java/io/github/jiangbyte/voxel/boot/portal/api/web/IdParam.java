package io.github.jiangbyte.voxel.boot.portal.api.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 单 ID 入参。
 */
@Data
public class IdParam {
    @NotBlank
    @Schema(description = "主键ID")
    private String id;
}

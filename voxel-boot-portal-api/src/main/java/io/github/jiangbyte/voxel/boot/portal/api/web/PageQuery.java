package io.github.jiangbyte.voxel.boot.portal.api.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询基类。
 */
@Data
public class PageQuery {
    @Min(1)
    @Schema(description = "页码，从 1 开始")
    private long current = 1;

    @Min(1)
    @Max(200)
    @Schema(description = "每页条数")
    private long size = 10;
}

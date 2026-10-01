package io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务启停入参。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "任务启停入参")
public class SysJobEnabledParam {
    @NotBlank
    private String id;
    @NotNull
    private Integer enabled;
}

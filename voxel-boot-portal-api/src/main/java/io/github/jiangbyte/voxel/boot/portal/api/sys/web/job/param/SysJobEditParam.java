package io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 更新任务入参。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "更新任务入参")
public class SysJobEditParam {
    @NotBlank
    private String id;
    @NotBlank @Size(max = 128)
    private String name;
    @NotBlank @Size(max = 255)
    private String handler;
    @NotBlank @Size(max = 16)
    private String triggerType;
    @NotBlank @Size(max = 255)
    private String triggerConfig;
    private Map<String, Object> params;
    @Size(max = 500)
    private String description;
    private Integer sort;
    private Integer enabled;
}

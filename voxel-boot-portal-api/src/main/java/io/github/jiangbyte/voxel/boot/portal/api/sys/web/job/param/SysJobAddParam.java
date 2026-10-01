package io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 创建任务入参。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "创建任务入参")
public class SysJobAddParam {
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
    private Integer sort = 0;
    private Integer enabled = 1;
}

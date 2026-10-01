package io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "任务响应")
public class SysJobResult {
    private String id;
    private String name;
    private String handler;
    private String triggerType;
    private String triggerConfig;
    private Map<String, Object> params;
    private LocalDateTime lastRunTime;
    private LocalDateTime nextRunTime;
    private String lastResult;
    private Integer enabled;
    private String description;
    private Integer sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

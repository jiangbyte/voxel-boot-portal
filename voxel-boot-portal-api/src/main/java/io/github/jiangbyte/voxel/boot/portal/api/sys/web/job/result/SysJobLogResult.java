package io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务日志响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "任务日志响应")
public class SysJobLogResult {
    private String id;
    private String jobId;
    private Map<String, Object> params;
    private LocalDateTime startedAt;
    private Long durationMs;
    private Integer success;
    private String result;
    private String executor;
    private String ip;
    private String processId;
    private String appDir;
    private LocalDateTime createdAt;
}

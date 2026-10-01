package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务执行日志只读视图。
 *
 * Author: Charlie
 */
public record JobLogView(
        String id,
        String jobId,
        Map<String, Object> params,
        LocalDateTime startedAt,
        Long durationMs,
        Integer success,
        String result,
        String executor,
        String ip,
        String processId,
        String appDir,
        LocalDateTime createdAt
) {
}

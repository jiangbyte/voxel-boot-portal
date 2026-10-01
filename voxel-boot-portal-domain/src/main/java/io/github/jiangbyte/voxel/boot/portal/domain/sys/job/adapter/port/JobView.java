package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务只读视图。
 *
 * Author: Charlie
 */
public record JobView(
        String id,
        String name,
        String handler,
        String triggerType,
        String triggerConfig,
        Map<String, Object> params,
        LocalDateTime lastRunTime,
        LocalDateTime nextRunTime,
        String lastResult,
        Integer enabled,
        String description,
        Integer sort,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

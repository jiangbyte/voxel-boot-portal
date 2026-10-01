package io.github.jiangbyte.voxel.boot.portal.cases.sys.job;

import java.util.Map;

/**
 * 更新任务命令。
 *
 * Author: Charlie
 */
public record UpdateJobCommand(
        String id,
        String name,
        String handler,
        String triggerType,
        String triggerConfig,
        Map<String, Object> params,
        String description,
        Integer sort,
        Integer enabled
) {
}

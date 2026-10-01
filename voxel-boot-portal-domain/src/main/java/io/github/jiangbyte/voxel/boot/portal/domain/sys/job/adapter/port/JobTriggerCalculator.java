package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

import java.time.LocalDateTime;

/**
 * 任务触发时间计算端口（由基础设施基于 CRON/FIXED 实现）。
 *
 * Author: Charlie
 */
public interface JobTriggerCalculator {

    void validate(String triggerType, String triggerConfig);

    LocalDateTime computeNextRunTime(String triggerType, String triggerConfig, LocalDateTime from);
}

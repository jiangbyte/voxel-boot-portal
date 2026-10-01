package io.github.jiangbyte.voxel.boot.portal.infrastructure.job;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobTriggerCalculator;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 任务触发时间计算：CRON / FIXED。
 *
 * Author: Charlie
 */
@Component
public class JobTriggerCalculatorImpl implements JobTriggerCalculator {

    public static final String TYPE_CRON = "CRON";
    public static final String TYPE_FIXED = "FIXED";

    @Override
    public void validate(String triggerType, String triggerConfig) {
        // 1. FIXED：正整数秒
        if (TYPE_FIXED.equalsIgnoreCase(triggerType)) {
            if (triggerConfig == null || !triggerConfig.trim().matches("\\d+")
                    || Long.parseLong(triggerConfig.trim()) <= 0) {
                throw new DomainException("固定间隔必须为正整数（秒）");
            }
            return;
        }
        // 2. 仅支持 CRON/FIXED
        if (!TYPE_CRON.equalsIgnoreCase(triggerType)) {
            throw new DomainException("触发类型仅支持 " + TYPE_CRON + " 或 " + TYPE_FIXED);
        }
        // 3. 解析 CRON
        try {
            if (CronExpression.parse(triggerConfig).next(ZonedDateTime.now()) == null) {
                throw new DomainException("CRON 表达式在未来没有可执行时间");
            }
        } catch (IllegalArgumentException ex) {
            throw new DomainException("CRON 表达式无效: " + ex.getMessage());
        }
    }

    @Override
    public LocalDateTime computeNextRunTime(String triggerType, String triggerConfig, LocalDateTime from) {
        if (TYPE_FIXED.equalsIgnoreCase(triggerType)) {
            return from.plusSeconds(Long.parseLong(triggerConfig.trim()));
        }
        ZonedDateTime next = CronExpression.parse(triggerConfig).next(from.atZone(java.time.ZoneId.systemDefault()));
        return next == null ? from : next.toLocalDateTime();
    }
}

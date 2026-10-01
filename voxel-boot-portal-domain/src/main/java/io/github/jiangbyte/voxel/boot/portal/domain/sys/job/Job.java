package io.github.jiangbyte.voxel.boot.portal.domain.sys.job;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.event.JobCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.event.JobDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.event.JobUpdated;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 定时任务聚合根：维护触发配置、启停与运行结果摘要。
 *
 * Author: Charlie
 */
public class Job extends AggregateRoot<String> {

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

    private Job() {
    }

    /**
     * 新建任务定义。
     */
    public static Job create(String name,
                             String handler,
                             String triggerType,
                             String triggerConfig,
                             Map<String, Object> params,
                             String description,
                             Integer sort,
                             Integer enabled,
                             LocalDateTime nextRunTime) {
        // 1. 校验必填
        requireText(name, "Job name");
        requireText(handler, "Job handler");
        requireText(triggerType, "Trigger type");
        requireText(triggerConfig, "Trigger config");
        // 2. 组装聚合
        Job job = new Job();
        job.id = UUID.randomUUID().toString().replace("-", "");
        job.name = name.trim();
        job.handler = handler.trim();
        job.triggerType = triggerType.trim().toUpperCase();
        job.triggerConfig = triggerConfig.trim();
        job.params = params;
        job.description = blankToNull(description);
        job.sort = sort == null ? 0 : sort;
        job.enabled = enabled == null ? 1 : Flags.of(enabled);
        job.nextRunTime = nextRunTime == null ? LocalDateTime.now() : nextRunTime;
        // 3. 登记创建事件
        job.registerEvent(new JobCreated(job.id));
        return job;
    }

    /**
     * 更新任务定义。
     */
    public void update(String name,
                       String handler,
                       String triggerType,
                       String triggerConfig,
                       Map<String, Object> params,
                       String description,
                       Integer sort,
                       Integer enabled,
                       LocalDateTime nextRunTimeIfTriggerChanged) {
        // 1. 校验必填
        requireText(name, "Job name");
        requireText(handler, "Job handler");
        requireText(triggerType, "Trigger type");
        requireText(triggerConfig, "Trigger config");
        // 2. 判断触发配置是否变更
        boolean triggerChanged = !this.triggerType.equalsIgnoreCase(triggerType.trim())
                || !this.triggerConfig.equals(triggerConfig.trim());
        // 3. 写回字段
        this.name = name.trim();
        this.handler = handler.trim();
        this.triggerType = triggerType.trim().toUpperCase();
        this.triggerConfig = triggerConfig.trim();
        this.params = params;
        this.description = blankToNull(description);
        if (sort != null) {
            this.sort = sort;
        }
        if (enabled != null) {
            this.enabled = enabled;
        }
        if (triggerChanged && nextRunTimeIfTriggerChanged != null) {
            this.nextRunTime = nextRunTimeIfTriggerChanged;
        }
        // 4. 登记更新事件
        registerEvent(new JobUpdated(this.id));
    }

    /**
     * 启停任务；重新启用时重置下次执行时间。
     */
    public void updateEnabled(Integer enabled, LocalDateTime nextRunTimeWhenEnable) {
        // 1. 写启用标志
        this.enabled = enabled;
        // 2. 启用时重置调度时间，避免沿用过期 nextRunTime
        if (Flags.isOn(enabled) && nextRunTimeWhenEnable != null) {
            this.nextRunTime = nextRunTimeWhenEnable;
        }
        // 3. 登记更新事件
        registerEvent(new JobUpdated(this.id));
    }

    /**
     * 记录一次执行结果。
     */
    public void markRun(LocalDateTime startedAt, LocalDateTime nextRunTime, String result) {
        this.lastRunTime = startedAt;
        this.nextRunTime = nextRunTime;
        this.lastResult = truncate(result, 500);
        registerEvent(new JobUpdated(this.id));
    }

    /** 登记删除事件。 */
    public void markDeleted() {
        registerEvent(new JobDeleted(this.id));
    }

    public static Job rehydrate(String id, String name, String handler, String triggerType, String triggerConfig,
                                Map<String, Object> params, LocalDateTime lastRunTime, LocalDateTime nextRunTime,
                                String lastResult, Integer enabled, String description, Integer sort) {
        Job job = new Job();
        job.id = id;
        job.name = name;
        job.handler = handler;
        job.triggerType = triggerType;
        job.triggerConfig = triggerConfig;
        job.params = params;
        job.lastRunTime = lastRunTime;
        job.nextRunTime = nextRunTime;
        job.lastResult = lastResult;
        job.enabled = enabled;
        job.description = description;
        job.sort = sort;
        return job;
    }

    @Override
    public String getId() { return id; }
    public String getName() { return name; }
    public String getHandler() { return handler; }
    public String getTriggerType() { return triggerType; }
    public String getTriggerConfig() { return triggerConfig; }
    public Map<String, Object> getParams() { return params; }
    public LocalDateTime getLastRunTime() { return lastRunTime; }
    public LocalDateTime getNextRunTime() { return nextRunTime; }
    public String getLastResult() { return lastResult; }
    public Integer getEnabled() { return enabled; }
    public String getDescription() { return description; }
    public Integer getSort() { return sort; }

    private static void requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new DomainException(label + " is required");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }
}

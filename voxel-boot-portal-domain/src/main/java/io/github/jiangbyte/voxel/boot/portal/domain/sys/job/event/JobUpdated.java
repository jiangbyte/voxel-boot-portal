package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 任务已更新领域事件。
 *
 * Author: Charlie
 */
public record JobUpdated(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public JobUpdated(String jobId) {
        this(jobId, LocalDateTime.now());
    }
}

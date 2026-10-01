package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 任务已创建领域事件。
 *
 * Author: Charlie
 */
public record JobCreated(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public JobCreated(String jobId) {
        this(jobId, LocalDateTime.now());
    }
}

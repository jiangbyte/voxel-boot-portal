package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 任务已删除领域事件。
 *
 * Author: Charlie
 */
public record JobDeleted(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public JobDeleted(String jobId) {
        this(jobId, LocalDateTime.now());
    }
}

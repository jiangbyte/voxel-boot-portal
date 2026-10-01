package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 部门已更新。
 *
 * Author: Charlie
 */
public record DeptUpdated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public DeptUpdated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

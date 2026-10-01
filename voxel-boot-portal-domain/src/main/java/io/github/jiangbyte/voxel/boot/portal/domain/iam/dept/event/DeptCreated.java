package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 部门已创建。
 *
 * Author: Charlie
 */
public record DeptCreated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public DeptCreated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

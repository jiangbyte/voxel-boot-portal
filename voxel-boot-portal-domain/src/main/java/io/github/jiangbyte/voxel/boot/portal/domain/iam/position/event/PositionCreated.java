package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 岗位已创建。
 *
 * Author: Charlie
 */
public record PositionCreated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public PositionCreated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

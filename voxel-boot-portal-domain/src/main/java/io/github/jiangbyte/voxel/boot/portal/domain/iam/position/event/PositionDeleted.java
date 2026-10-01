package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 岗位已删除。
 *
 * Author: Charlie
 */
public record PositionDeleted(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public PositionDeleted(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

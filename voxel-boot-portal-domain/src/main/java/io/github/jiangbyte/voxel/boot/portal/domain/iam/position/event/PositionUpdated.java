package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 岗位已更新。
 *
 * Author: Charlie
 */
public record PositionUpdated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public PositionUpdated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

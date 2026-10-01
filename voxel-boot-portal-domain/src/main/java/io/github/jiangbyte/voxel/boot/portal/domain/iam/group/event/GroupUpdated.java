package io.github.jiangbyte.voxel.boot.portal.domain.iam.group.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 用户组已更新。
 *
 * Author: Charlie
 */
public record GroupUpdated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public GroupUpdated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

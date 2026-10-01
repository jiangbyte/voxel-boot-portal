package io.github.jiangbyte.voxel.boot.portal.domain.iam.group.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 用户组已创建。
 *
 * Author: Charlie
 */
public record GroupCreated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public GroupCreated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

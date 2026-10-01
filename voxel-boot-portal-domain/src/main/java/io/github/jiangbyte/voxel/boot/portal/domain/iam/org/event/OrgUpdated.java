package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 组织已更新。
 *
 * Author: Charlie
 */
public record OrgUpdated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public OrgUpdated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

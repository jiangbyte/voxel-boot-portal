package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 组织已删除。
 *
 * Author: Charlie
 */
public record OrgDeleted(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public OrgDeleted(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

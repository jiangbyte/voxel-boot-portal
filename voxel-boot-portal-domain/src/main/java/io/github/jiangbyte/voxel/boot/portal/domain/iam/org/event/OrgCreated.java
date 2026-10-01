package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 组织已创建。
 *
 * Author: Charlie
 */
public record OrgCreated(String aggregateId, String name, LocalDateTime occurredAt) implements DomainEvent {

    public OrgCreated(String aggregateId, String name) {
        this(aggregateId, name, LocalDateTime.now());
    }
}

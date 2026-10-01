package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 资源已更新。
 *
 * Author: Charlie
 */
public record ResourceUpdated(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public ResourceUpdated(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

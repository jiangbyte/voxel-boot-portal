package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 资源已创建。
 *
 * Author: Charlie
 */
public record ResourceCreated(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public ResourceCreated(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

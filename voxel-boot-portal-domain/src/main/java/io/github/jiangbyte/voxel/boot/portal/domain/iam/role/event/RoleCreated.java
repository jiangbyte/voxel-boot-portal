package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 角色已创建。
 *
 * Author: Charlie
 */
public record RoleCreated(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public RoleCreated(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

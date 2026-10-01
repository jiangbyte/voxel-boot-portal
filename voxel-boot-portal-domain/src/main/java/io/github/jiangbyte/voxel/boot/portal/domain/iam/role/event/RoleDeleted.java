package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 角色已删除。
 *
 * Author: Charlie
 */
public record RoleDeleted(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public RoleDeleted(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

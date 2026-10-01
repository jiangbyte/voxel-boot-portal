package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 角色已更新。
 *
 * Author: Charlie
 */
public record RoleUpdated(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public RoleUpdated(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 管理端资料已更新。
 *
 * Author: Charlie
 */
public record AdminProfileUpdated(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public AdminProfileUpdated(String aggregateId) {
        this(aggregateId, LocalDateTime.now());
    }
}

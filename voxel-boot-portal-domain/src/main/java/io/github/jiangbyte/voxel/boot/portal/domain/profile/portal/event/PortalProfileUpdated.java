package io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 门户资料已更新。
 *
 * Author: Charlie
 */
public record PortalProfileUpdated(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public PortalProfileUpdated(String accountId) {
        this(accountId, LocalDateTime.now());
    }
}

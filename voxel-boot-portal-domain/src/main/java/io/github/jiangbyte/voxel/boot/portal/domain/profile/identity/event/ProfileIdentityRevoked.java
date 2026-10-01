package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 实名快照已撤销。
 *
 * Author: Charlie
 */
public record ProfileIdentityRevoked(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public ProfileIdentityRevoked(String accountId) {
        this(accountId, LocalDateTime.now());
    }
}

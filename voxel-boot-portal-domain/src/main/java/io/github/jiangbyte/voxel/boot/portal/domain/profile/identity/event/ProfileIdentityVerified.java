package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 实名快照已认证通过。
 *
 * Author: Charlie
 */
public record ProfileIdentityVerified(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public ProfileIdentityVerified(String accountId) {
        this(accountId, LocalDateTime.now());
    }
}

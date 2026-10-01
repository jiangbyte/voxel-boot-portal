package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 账号已注销。
 *
 * Author: Charlie
 */
public record AccountCancelled(String aggregateId,
                               String cancelledBy,
                               String cancelReason,
                               LocalDateTime occurredAt) implements DomainEvent {

    public AccountCancelled(String aggregateId, String cancelledBy, String cancelReason) {
        this(aggregateId, cancelledBy, cancelReason, LocalDateTime.now());
    }
}

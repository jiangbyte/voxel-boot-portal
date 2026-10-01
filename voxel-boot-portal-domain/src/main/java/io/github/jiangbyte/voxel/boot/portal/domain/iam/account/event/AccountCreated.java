package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 账号已创建。
 *
 * Author: Charlie
 */
public record AccountCreated(String aggregateId,
                             String accountType,
                             LocalDateTime occurredAt) implements DomainEvent {

    public AccountCreated(String aggregateId, String accountType) {
        this(aggregateId, accountType, LocalDateTime.now());
    }
}

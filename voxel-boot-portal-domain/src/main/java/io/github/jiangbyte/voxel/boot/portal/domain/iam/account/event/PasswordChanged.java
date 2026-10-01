package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 账号密码已变更。
 *
 * Author: Charlie
 */
public record PasswordChanged(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public PasswordChanged(String aggregateId) {
        this(aggregateId, LocalDateTime.now());
    }
}

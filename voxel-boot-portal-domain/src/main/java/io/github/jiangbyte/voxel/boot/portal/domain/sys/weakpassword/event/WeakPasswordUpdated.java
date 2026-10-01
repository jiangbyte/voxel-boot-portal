package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 弱密码项已更新。
 *
 * Author: Charlie
 */
public record WeakPasswordUpdated(String aggregateId, String password, LocalDateTime occurredAt) implements DomainEvent {

    public WeakPasswordUpdated(String aggregateId, String password) {
        this(aggregateId, password, LocalDateTime.now());
    }
}

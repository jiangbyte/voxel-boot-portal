package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 弱密码项已创建。
 *
 * Author: Charlie
 */
public record WeakPasswordCreated(String aggregateId, String password, LocalDateTime occurredAt) implements DomainEvent {

    public WeakPasswordCreated(String aggregateId, String password) {
        this(aggregateId, password, LocalDateTime.now());
    }
}

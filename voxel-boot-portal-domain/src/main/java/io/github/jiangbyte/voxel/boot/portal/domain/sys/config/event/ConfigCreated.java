package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 系统配置已创建。
 *
 * Author: Charlie
 */
public record ConfigCreated(String aggregateId, String configKey, LocalDateTime occurredAt) implements DomainEvent {

    public ConfigCreated(String aggregateId, String configKey) {
        this(aggregateId, configKey, LocalDateTime.now());
    }
}

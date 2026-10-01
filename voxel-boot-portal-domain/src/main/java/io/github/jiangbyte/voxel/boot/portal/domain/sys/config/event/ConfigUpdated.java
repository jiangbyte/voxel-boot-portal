package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 系统配置已更新。
 *
 * Author: Charlie
 */
public record ConfigUpdated(String aggregateId, String configKey, LocalDateTime occurredAt) implements DomainEvent {

    public ConfigUpdated(String aggregateId, String configKey) {
        this(aggregateId, configKey, LocalDateTime.now());
    }
}

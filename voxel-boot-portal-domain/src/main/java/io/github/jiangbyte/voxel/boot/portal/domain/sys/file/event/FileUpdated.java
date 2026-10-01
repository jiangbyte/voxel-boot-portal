package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 文件元数据已更新。
 *
 * Author: Charlie
 */
public record FileUpdated(String aggregateId, String objectName, LocalDateTime occurredAt) implements DomainEvent {

    public FileUpdated(String aggregateId, String objectName) {
        this(aggregateId, objectName, LocalDateTime.now());
    }
}

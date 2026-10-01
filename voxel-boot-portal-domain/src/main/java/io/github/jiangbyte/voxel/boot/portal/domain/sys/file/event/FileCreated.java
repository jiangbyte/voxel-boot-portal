package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 文件元数据已创建。
 *
 * Author: Charlie
 */
public record FileCreated(String aggregateId, String objectName, LocalDateTime occurredAt) implements DomainEvent {

    public FileCreated(String aggregateId, String objectName) {
        this(aggregateId, objectName, LocalDateTime.now());
    }
}

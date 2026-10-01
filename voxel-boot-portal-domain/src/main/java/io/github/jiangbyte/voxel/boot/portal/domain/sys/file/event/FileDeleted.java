package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 文件元数据已删除。
 *
 * Author: Charlie
 */
public record FileDeleted(String aggregateId, String objectName, LocalDateTime occurredAt) implements DomainEvent {

    public FileDeleted(String aggregateId, String objectName) {
        this(aggregateId, objectName, LocalDateTime.now());
    }
}

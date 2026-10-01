package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 字典项已删除。
 *
 * Author: Charlie
 */
public record DictDeleted(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public DictDeleted(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

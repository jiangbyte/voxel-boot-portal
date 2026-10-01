package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 字典项已创建。
 *
 * Author: Charlie
 */
public record DictCreated(String aggregateId, String code, LocalDateTime occurredAt) implements DomainEvent {

    public DictCreated(String aggregateId, String code) {
        this(aggregateId, code, LocalDateTime.now());
    }
}

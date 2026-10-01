package io.github.jiangbyte.voxel.boot.portal.infrastructure.log;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

/**
 * 领域事件 Outbox 写入端口：供发布器落库/入队，后续可由持久化实现替换。
 *
 * Author: Charlie
 */
public interface DomainEventOutboxWriter {

    /**
     * 写入单条待投递领域事件。
     */
    void append(DomainEvent event);
}

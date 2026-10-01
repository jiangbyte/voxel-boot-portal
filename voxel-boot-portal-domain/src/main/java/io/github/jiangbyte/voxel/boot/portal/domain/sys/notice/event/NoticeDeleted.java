package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 公告已删除。
 *
 * Author: Charlie
 */
public record NoticeDeleted(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public NoticeDeleted(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}

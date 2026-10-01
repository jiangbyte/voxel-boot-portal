package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 公告已创建。
 *
 * Author: Charlie
 */
public record NoticeCreated(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public NoticeCreated(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}

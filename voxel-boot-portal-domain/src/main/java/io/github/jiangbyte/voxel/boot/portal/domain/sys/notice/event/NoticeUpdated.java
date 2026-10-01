package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 公告已更新。
 *
 * Author: Charlie
 */
public record NoticeUpdated(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public NoticeUpdated(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}

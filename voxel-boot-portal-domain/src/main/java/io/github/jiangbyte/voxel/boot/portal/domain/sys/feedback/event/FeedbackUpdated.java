package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 反馈已更新（状态或回复）。
 *
 * Author: Charlie
 */
public record FeedbackUpdated(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public FeedbackUpdated(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}

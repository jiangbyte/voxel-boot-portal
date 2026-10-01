package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 反馈已提交。
 *
 * Author: Charlie
 */
public record FeedbackCreated(String aggregateId, String title, LocalDateTime occurredAt) implements DomainEvent {

    public FeedbackCreated(String aggregateId, String title) {
        this(aggregateId, title, LocalDateTime.now());
    }
}

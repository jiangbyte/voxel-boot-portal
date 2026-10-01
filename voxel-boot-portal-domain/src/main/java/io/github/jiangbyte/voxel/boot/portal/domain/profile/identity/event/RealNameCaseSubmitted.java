package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 实名工单已提交。
 *
 * Author: Charlie
 */
public record RealNameCaseSubmitted(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public RealNameCaseSubmitted(String caseId) {
        this(caseId, LocalDateTime.now());
    }
}

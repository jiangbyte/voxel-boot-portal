package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 实名工单已审结（通过/驳回）。
 *
 * Author: Charlie
 */
public record RealNameCaseDecided(String aggregateId, String status, LocalDateTime occurredAt) implements DomainEvent {

    public RealNameCaseDecided(String caseId, String status) {
        this(caseId, status, LocalDateTime.now());
    }
}

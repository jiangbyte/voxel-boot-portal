package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 代码生成方案已更新。
 *
 * Author: Charlie
 */
public record CodegenPlanUpdated(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public CodegenPlanUpdated(String planId) {
        this(planId, LocalDateTime.now());
    }
}

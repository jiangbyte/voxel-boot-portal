package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 代码生成方案已删除。
 *
 * Author: Charlie
 */
public record CodegenPlanDeleted(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public CodegenPlanDeleted(String planId) {
        this(planId, LocalDateTime.now());
    }
}

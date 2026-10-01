package io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;

/**
 * 代码生成方案已创建。
 *
 * Author: Charlie
 */
public record CodegenPlanCreated(String aggregateId, LocalDateTime occurredAt) implements DomainEvent {

    public CodegenPlanCreated(String planId) {
        this(planId, LocalDateTime.now());
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

import java.time.LocalDateTime;

/**
 * 领域事件：描述聚合内已发生的事实。
 *
 * Author: Charlie
 */
public interface DomainEvent {

    /**
     * 事件发生时间。
     */
    LocalDateTime occurredAt();

    /**
     * 产生事件的聚合标识（字符串形态，便于跨上下文传递）。
     */
    String aggregateId();
}

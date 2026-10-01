package io.github.jiangbyte.voxel.boot.portal.infrastructure.log;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * 领域事件发布实现：记录日志并写入内存 Outbox，便于联调与后续替换为持久化 Outbox。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class LoggingDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    private final DomainEventOutboxWriter outboxWriter;


    @Override
    public void publish(DomainEvent event) {
        // 1. 空事件直接返回
        if (event == null) {
            return;
        }
        // 2. 结构化日志，保留原有联调可见性
        log.info("DomainEvent published: type={}, aggregateId={}, occurredAt={}",
                event.getClass().getSimpleName(),
                event.aggregateId(),
                event.occurredAt());
        // 3. 同步写入 Outbox 骨架
        outboxWriter.append(event);
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

/**
 * 领域事件发布端口：由基础设施（Outbox / 进程内总线）实现。
 *
 * Author: Charlie
 */
public interface DomainEventPublisher {

    /**
     * 发布单个领域事件。
     */
    void publish(DomainEvent event);

    /**
     * 批量发布领域事件。
     */
    default void publishAll(Iterable<? extends DomainEvent> events) {
        if (events == null) {
            return;
        }
        for (DomainEvent event : events) {
            publish(event);
        }
    }
}

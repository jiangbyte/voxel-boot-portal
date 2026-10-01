package io.github.jiangbyte.voxel.boot.portal.infrastructure.log;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 内存 Outbox 骨架：用 ConcurrentLinkedQueue 暂存领域事件，便于联调与后续替换为 DB Outbox。
 *
 * Author: Charlie
 */
@Component
public class InMemoryDomainEventOutbox implements DomainEventOutboxWriter {

    private final ConcurrentLinkedQueue<DomainEvent> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void append(DomainEvent event) {
        // 1. 空事件忽略
        if (event == null) {
            return;
        }
        // 2. 入队，供后续中继消费
        queue.offer(event);
    }

    /**
     * 查看当前队列快照（不消费）。
     */
    public List<DomainEvent> snapshot() {
        return List.copyOf(new ArrayList<>(queue));
    }

    /**
     * 弹出队首事件；队列为空返回 null。
     */
    public DomainEvent poll() {
        return queue.poll();
    }

    /**
     * 当前积压条数。
     */
    public int size() {
        return queue.size();
    }
}

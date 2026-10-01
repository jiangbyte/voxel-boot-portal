package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 聚合根事件登记与实体相等性单测。
 */
class AggregateRootTest {

    @Test
    void pullDomainEvents_clearsInternalList() {
        DemoAggregate aggregate = new DemoAggregate("1");
        aggregate.doSomething();
        List<DomainEvent> first = aggregate.pullDomainEvents();
        assertEquals(1, first.size());
        assertTrue(aggregate.pullDomainEvents().isEmpty());
    }

    @Test
    void entityEquals_byId() {
        DemoAggregate a = new DemoAggregate("x");
        DemoAggregate b = new DemoAggregate("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    static final class DemoAggregate extends AggregateRoot<String> {
        private final String id;

        DemoAggregate(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        void doSomething() {
            registerEvent(new DemoEvent(id));
        }
    }

    record DemoEvent(String aggregateId) implements DomainEvent {
        @Override
        public LocalDateTime occurredAt() {
            return LocalDateTime.now();
        }
    }
}

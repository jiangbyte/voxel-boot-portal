package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类：维护领域事件登记，由仓储保存后统一派发。
 *
 * Author: Charlie
 */
public abstract class AggregateRoot<ID> extends Entity<ID> {

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 登记领域事件，供事务提交后派发。
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * 取出并清空已登记事件（派发侧消费）。
     */
    public List<DomainEvent> pullDomainEvents() {
        // 1. 复制当前事件列表
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        // 2. 清空内部登记，避免重复派发
        domainEvents.clear();
        // 3. 返回不可变视图，防止外部篡改
        return Collections.unmodifiableList(events);
    }

    /**
     * 查看尚未派发的事件（只读）。
     */
    public List<DomainEvent> peekDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}

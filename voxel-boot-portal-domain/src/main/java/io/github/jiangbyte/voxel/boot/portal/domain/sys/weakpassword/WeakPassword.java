package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.event.WeakPasswordCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.event.WeakPasswordDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.event.WeakPasswordUpdated;

import java.util.UUID;

/**
 * 弱密码聚合根：维护拒绝列表中的明文口令及其唯一性不变量。
 *
 * Author: Charlie
 */
public class WeakPassword extends AggregateRoot<String> {

    private String id;
    private String password;

    private WeakPassword() {
    }

    /**
     * 新建弱密码项并登记创建事件。
     */
    public static WeakPassword create(String password) {
        // 1. 校验口令非空并 trim，保证唯一性比较口径一致
        String normalized = requirePassword(password);
        // 2. 组装聚合并分配无横线 UUID 标识
        WeakPassword aggregate = new WeakPassword();
        aggregate.id = UUID.randomUUID().toString().replace("-", "");
        aggregate.password = normalized;
        // 3. 登记领域事件供仓储派发
        aggregate.registerEvent(new WeakPasswordCreated(aggregate.id, aggregate.password));
        return aggregate;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static WeakPassword reconstitute(String id, String password) {
        WeakPassword aggregate = new WeakPassword();
        aggregate.id = id;
        aggregate.password = password;
        return aggregate;
    }

    /**
     * 更新弱口令明文并登记更新事件。
     */
    public void update(String password) {
        // 1. 校验并规范化口令
        this.password = requirePassword(password);
        // 2. 登记更新事件
        registerEvent(new WeakPasswordUpdated(this.id, this.password));
    }

    /**
     * 标记删除并登记删除事件（由应用层在移除前调用）。
     */
    public void markDeleted() {
        registerEvent(new WeakPasswordDeleted(this.id, this.password));
    }

    private static String requirePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new DomainException("Weak password must not be blank");
        }
        return password.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

import java.util.Objects;

/**
 * 领域实体基类：以标识判定相等，不含持久化注解。
 *
 * Author: Charlie
 */
public abstract class Entity<ID> {

    /**
     * 返回实体唯一标识。
     */
    public abstract ID getId();

    @Override
    public final boolean equals(Object o) {
        // 1. 同引用直接相等
        if (this == o) {
            return true;
        }
        // 2. 类型不一致则不等
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        // 3. 双方均有 ID 时按 ID 比较；任一 ID 为空则仅同引用才相等（已在步骤 1 处理）
        Entity<?> other = (Entity<?>) o;
        ID id = getId();
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }
}

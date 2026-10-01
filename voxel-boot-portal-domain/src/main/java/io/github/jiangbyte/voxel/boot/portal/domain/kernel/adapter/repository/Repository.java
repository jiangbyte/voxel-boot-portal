package io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;

/**
 * 聚合仓储端口：按标识加载与保存，不暴露分页 CRUD。
 *
 * Author: Charlie
 */
public interface Repository<A extends AggregateRoot<ID>, ID> {

    /**
     * 按标识加载聚合；不存在则空。
     */
    Optional<A> findById(ID id);

    /**
     * 持久化聚合并派发其领域事件。
     */
    void save(A aggregate);

    /**
     * 按标识删除聚合（物理删除，与现库一致）。
     */
    void remove(ID id);
}

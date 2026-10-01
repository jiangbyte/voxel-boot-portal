package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.Position;

/**
 * 岗位聚合仓储端口。
 *
 * Author: Charlie
 */
public interface PositionRepository extends Repository<Position, String> {

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Position aggregate);
}

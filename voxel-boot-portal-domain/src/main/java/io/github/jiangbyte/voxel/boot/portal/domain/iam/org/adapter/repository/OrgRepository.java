package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.Org;

/**
 * 组织聚合仓储端口。
 *
 * Author: Charlie
 */
public interface OrgRepository extends Repository<Org, String> {

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Org aggregate);

    /**
     * 是否存在指定父节点的子组织。
     */
    boolean existsByParentId(String parentId);
}

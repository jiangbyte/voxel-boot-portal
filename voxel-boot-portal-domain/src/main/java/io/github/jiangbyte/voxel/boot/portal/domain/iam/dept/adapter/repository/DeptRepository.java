package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.Dept;

/**
 * 部门聚合仓储端口。
 *
 * Author: Charlie
 */
public interface DeptRepository extends Repository<Dept, String> {

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Dept aggregate);

    /**
     * 是否存在指定父节点的子部门。
     */
    boolean existsByParentId(String parentId);
}

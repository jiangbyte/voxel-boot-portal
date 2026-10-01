package io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;

/**
 * 用户组聚合仓储端口。
 *
 * Author: Charlie
 */
public interface GroupRepository extends Repository<Group, String> {

    /**
     * 按名称精确查找。
     */
    Optional<Group> findByName(String name);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Group aggregate);
}

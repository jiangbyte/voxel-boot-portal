package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;

/**
 * 角色聚合仓储端口。
 *
 * Author: Charlie
 */
public interface RoleRepository extends Repository<Role, String> {

    /**
     * 按编码精确查找。
     */
    Optional<Role> findByCode(String code);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Role aggregate);
}

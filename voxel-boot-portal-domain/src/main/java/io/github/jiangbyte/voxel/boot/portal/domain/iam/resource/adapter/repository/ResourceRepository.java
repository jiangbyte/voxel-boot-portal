package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;

/**
 * 资源聚合仓储端口。
 *
 * Author: Charlie
 */
public interface ResourceRepository extends Repository<Resource, String> {

    /**
     * 按模块与编码精确查找。
     */
    Optional<Resource> findByModuleIdAndCode(String moduleId, String code);

    /**
     * 是否存在指定父节点的子资源。
     */
    boolean existsByParentId(String parentId);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Resource aggregate);
}

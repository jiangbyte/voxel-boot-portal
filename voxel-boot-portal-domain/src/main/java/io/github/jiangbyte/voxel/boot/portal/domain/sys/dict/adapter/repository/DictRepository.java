package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.Dict;

/**
 * 字典聚合仓储端口。
 *
 * Author: Charlie
 */
public interface DictRepository extends Repository<Dict, String> {

    /**
     * 按编码精确查找字典项。
     */
    Optional<Dict> findByCode(String code);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Dict aggregate);
}

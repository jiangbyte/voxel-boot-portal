package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.Config;

/**
 * 系统配置聚合仓储端口。
 *
 * Author: Charlie
 */
public interface ConfigRepository extends Repository<Config, String> {

    /**
     * 按配置键精确查找。
     */
    Optional<Config> findByKey(String configKey);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Config aggregate);
}

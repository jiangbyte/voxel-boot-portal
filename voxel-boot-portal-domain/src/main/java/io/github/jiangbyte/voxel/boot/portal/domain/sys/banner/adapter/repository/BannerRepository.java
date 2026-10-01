package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 聚合仓储端口。
 *
 * Author: Charlie
 */
public interface BannerRepository extends Repository<Banner, String> {

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(Banner aggregate);
}

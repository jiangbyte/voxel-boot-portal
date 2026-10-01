package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 查询用例端口。
 *
 * Author: Charlie
 */
public interface BannerQueryService {

    /**
     * 按主键查询详情。
     */
    BannerView detail(String id);

    /**
     * 分页查询。
     */
    PageResult<BannerView> page(BannerPageQuery query);

    /**
     * 管理端可见列表（按投放位筛选）。
     */
    List<BannerView> adminList(String position, String category, String type);

    /**
     * 门户端可见列表（按投放位筛选）。
     */
    List<BannerView> portalList(String position, String category, String type);
}

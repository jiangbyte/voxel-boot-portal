package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

/**
 * 岗位查询用例端口。
 *
 * Author: Charlie
 */
public interface PositionQueryService {

    /**
     * 按主键查询详情。
     */
    PositionView detail(String id);

    /**
     * 分页查询。
     */
    PageResult<PositionView> page(PositionPageQuery query);
}

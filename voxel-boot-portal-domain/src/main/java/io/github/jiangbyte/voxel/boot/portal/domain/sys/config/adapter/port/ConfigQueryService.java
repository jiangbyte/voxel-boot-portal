package io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 系统配置查询用例端口。
 *
 * Author: Charlie
 */
public interface ConfigQueryService {

    /**
     * 按主键查询详情。
     */
    ConfigView detail(String id);

    /**
     * 分页查询。
     */
    PageResult<ConfigView> page(ConfigPageQuery query);

    /**
     * 按分类列出配置。
     */
    List<ConfigView> list(String category);
}

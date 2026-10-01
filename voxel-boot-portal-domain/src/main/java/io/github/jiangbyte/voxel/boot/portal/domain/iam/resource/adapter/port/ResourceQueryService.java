package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 资源查询用例端口。
 *
 * Author: Charlie
 */
public interface ResourceQueryService {

    /**
     * 按主键查询详情。
     */
    ResourceView detail(String id);

    /**
     * 分页查询。
     */
    PageResult<ResourceView> page(ResourcePageQuery query);

    /**
     * 构建资源树（排除 BUTTON/ACTION）。
     */
    List<Tree<String>> tree(ResourceTreeQuery query);

    /**
     * 当前账号可见资源列表（简化：返回全部启用非按钮资源）。
     */
    List<ResourceView> current();
}

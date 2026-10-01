package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 组织查询用例端口。
 *
 * Author: Charlie
 */
public interface OrgQueryService {

    /**
     * 按主键查询详情。
     */
    OrgView detail(String id);

    /**
     * 分页查询。
     */
    PageResult<OrgView> page(OrgPageQuery query);

    /**
     * 构建组织树。
     */
    List<Tree<String>> tree();
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.Collection;
import java.util.List;

/**
 * 部门查询用例端口。
 *
 * Author: Charlie
 */
public interface DeptQueryService {

    /**
     * 按主键查询详情。
     */
    DeptView detail(String id);

    /**
     * 按主键批量查询（IN 分批，禁止循环单查）。
     */
    List<DeptView> listByIds(Collection<String> ids);

    /**
     * 分页查询。
     */
    PageResult<DeptView> page(DeptPageQuery query);

    /**
     * 构建部门树。
     */
    List<Tree<String>> tree();
}

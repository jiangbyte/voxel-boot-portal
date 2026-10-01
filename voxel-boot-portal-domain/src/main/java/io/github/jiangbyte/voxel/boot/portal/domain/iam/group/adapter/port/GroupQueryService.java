package io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.Collection;
import java.util.List;

/**
 * 用户组查询用例端口。
 *
 * Author: Charlie
 */
public interface GroupQueryService {

    /**
     * 按主键查询详情。
     */
    GroupView detail(String id);

    /**
     * 按主键批量查询（IN 分批，禁止循环单查）。
     */
    List<GroupView> listByIds(Collection<String> ids);

    /**
     * 分页查询。
     */
    PageResult<GroupView> page(GroupPageQuery query);
}

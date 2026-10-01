package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.Collection;
import java.util.List;

/**
 * 角色查询用例端口。
 *
 * Author: Charlie
 */
public interface RoleQueryService {

    /**
     * 按主键查询详情。
     */
    RoleView detail(String id);

    /**
     * 按主键批量查询（IN 分批，禁止循环单查）。
     */
    List<RoleView> listByIds(Collection<String> ids);

    /**
     * 分页查询。
     */
    PageResult<RoleView> page(RolePageQuery query);
}

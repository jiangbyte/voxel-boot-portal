package io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 弱密码查询用例端口：详情、分页与列表。
 *
 * Author: Charlie
 */
public interface WeakPasswordQueryService {

    /**
     * 按主键查询详情。
     */
    WeakPasswordView detail(String id);

    /**
     * 分页查询弱密码项。
     */
    PageResult<WeakPasswordView> page(WeakPasswordPageQuery query);

    /**
     * 列表查询弱密码项。
     */
    List<WeakPasswordView> list(String password, String keyword);
}

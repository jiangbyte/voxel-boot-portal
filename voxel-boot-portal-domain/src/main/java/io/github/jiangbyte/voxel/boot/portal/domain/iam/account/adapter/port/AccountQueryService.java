package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

/**
 * 账号查询用例端口。
 *
 * Author: Charlie
 */
public interface AccountQueryService {

    /**
     * 按主键查询详情。
     */
    AccountView detail(String id);

    /**
     * 分页查询账号。
     */
    io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult<AccountView> page(AccountPageQuery query);
}

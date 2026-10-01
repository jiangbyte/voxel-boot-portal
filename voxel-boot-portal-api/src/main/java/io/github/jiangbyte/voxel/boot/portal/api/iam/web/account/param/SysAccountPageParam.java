package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账号分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "账号分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAccountPageParam extends PageQuery {

    @Schema(description = "登录账号（模糊）")
    private String account;

    @Schema(description = "账户类型：ADMIN / PORTAL")
    private String accountType;

    @Schema(description = "账户状态：ENABLED / LOCKED / CANCELLED")
    private String accountStatus;
}

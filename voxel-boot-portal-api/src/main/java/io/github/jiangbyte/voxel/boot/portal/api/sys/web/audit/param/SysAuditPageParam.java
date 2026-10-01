package io.github.jiangbyte.voxel.boot.portal.api.sys.web.audit.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作审计分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "操作审计分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAuditPageParam extends PageQuery {
    @Schema(description = "业务模块")
    private String module;
    @Schema(description = "动作")
    private String action;
    @Schema(description = "排除指定 action")
    private String excludeAction;
    @Schema(description = "账户ID")
    private String accountId;
    @Schema(description = "是否成功")
    private Integer success;
}

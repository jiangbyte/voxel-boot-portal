package io.github.jiangbyte.voxel.boot.portal.api.sys.web.weakpassword.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 弱密码分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "弱密码分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysWeakPasswordPageParam extends PageQuery {

    @Schema(description = "弱口令明文（用于注册/改密校验）")
    private String password;

    @Schema(description = "关键字")
    private String keyword;
}

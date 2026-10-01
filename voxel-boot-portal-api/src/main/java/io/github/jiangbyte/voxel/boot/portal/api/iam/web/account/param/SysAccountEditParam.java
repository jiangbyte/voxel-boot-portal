package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理端编辑账号入参。
 *
 * Author: Charlie
 */
@Schema(description = "管理端编辑账号入参")
@Data
public class SysAccountEditParam {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "主键ID")
    private String id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,64}$", message = "账号仅允许字母、数字和下划线，长度 3-64")
    @Schema(description = "登录账号/用户名")
    private String account;

    @Schema(description = "明文密码（空则不改）")
    private String password;

    @NotBlank
    @Schema(description = "账户类型：ADMIN / PORTAL")
    private String accountType;

    @Schema(description = "账户状态：ENABLED / LOCKED")
    private String accountStatus = "ENABLED";
}

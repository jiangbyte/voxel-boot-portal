package io.github.jiangbyte.voxel.boot.portal.api.auth.web.oauth.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理端解绑账号 OAuth")
@Data
public class AdminOauthUnbindParam {
    @NotBlank
    @Schema(description = "账号 ID")
    private String accountId;
    @NotBlank
    @Schema(description = "提供商")
    private String provider;
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录/刷新响应。
 *
 * Author: Charlie
 */
@Schema(description = "登录响应")
@Data
public class LoginHttpResult {

    @Schema(description = "会话 token")
    private String token;
    @Schema(description = "账号 ID")
    private String accountId;
    @Schema(description = "账户类型")
    private String accountType;
    @Schema(description = "密码是否过期")
    private Integer passwordExpired = 0;
    @Schema(description = "是否需强制绑定邮箱")
    private Integer forceBindEmail = 0;
    @Schema(description = "是否需强制绑定手机")
    private Integer forceBindPhone = 0;
    @Schema(description = "密码过期告警剩余天数")
    private Integer passwordExpiryWarningDays;
    @Schema(description = "token TTL 秒")
    private Long expiresIn;

    public static LoginHttpResult from(LoginResult result) {
        if (result == null) {
            return null;
        }
        LoginHttpResult http = new LoginHttpResult();
        http.setToken(result.getToken());
        http.setAccountId(result.getAccountId());
        http.setAccountType(result.getAccountType());
        http.setPasswordExpired(result.getPasswordExpired());
        http.setForceBindEmail(result.getForceBindEmail());
        http.setForceBindPhone(result.getForceBindPhone());
        http.setPasswordExpiryWarningDays(result.getPasswordExpiryWarningDays());
        http.setExpiresIn(result.getExpiresIn());
        return http;
    }
}

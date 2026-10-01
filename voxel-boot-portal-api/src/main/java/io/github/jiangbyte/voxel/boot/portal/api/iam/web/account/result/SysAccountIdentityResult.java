package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 账号身份项 API 响应。
 */
@Schema(description = "账号身份项")
@Data
public class SysAccountIdentityResult {
    private String id;
    private String identityType;
    private String identifier;
    private Integer verified;
    private Integer isPrimary;
    private String bindStatus;
}

package io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 门户资料详情。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "门户资料详情")
public class UserProfileResult {
    private String accountId;
    private String nickname;
    private String avatar;
    private String signature;
    private String phone;
    private String email;
}

package io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 门户当前用户摘要。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "门户当前用户摘要")
public class MeResult {
    private String accountId;
    private String nickname;
    private String avatar;
    private UserProfileResult profile;
}

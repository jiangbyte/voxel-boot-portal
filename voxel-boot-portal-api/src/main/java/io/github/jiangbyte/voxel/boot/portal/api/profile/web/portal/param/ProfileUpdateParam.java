package io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 门户资料更新请求。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "门户资料更新请求")
public class ProfileUpdateParam {
    private String nickname;
    private String avatar;
    private String signature;
}

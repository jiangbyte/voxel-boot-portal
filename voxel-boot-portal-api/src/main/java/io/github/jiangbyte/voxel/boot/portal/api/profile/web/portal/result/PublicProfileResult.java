package io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 门户公开资料。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "门户公开资料")
public class PublicProfileResult {
    private String accountId;
    private String nickname;
    private String account;
    private String avatar;
    private String signature;
}

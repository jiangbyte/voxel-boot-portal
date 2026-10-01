package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "MeResult")
@Data
public class MeResult {
    private String accountId;
    private String nickname;
    private String avatar;
    private UserProfileResult profile;
}

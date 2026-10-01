package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "UserProfileResult")
@Data
public class UserProfileResult {
    private String accountId;
    private String nickname;
    private String avatar;
    private String signature;
    private String phone;
    private String email;
    private String remark;
}

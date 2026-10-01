package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "撤销实名")
public class IdentityRevokeParam {
    @NotBlank private String accountId;
    private String remark;
}

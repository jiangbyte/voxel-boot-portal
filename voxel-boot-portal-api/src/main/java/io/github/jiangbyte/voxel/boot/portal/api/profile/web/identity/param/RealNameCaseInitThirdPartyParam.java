package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "初始化第三方实名")
public class RealNameCaseInitThirdPartyParam {
    private String businessType;
    @NotBlank private String documentType;
    @NotBlank private String realName;
    @NotBlank private String documentNo;
    private String provider;
}

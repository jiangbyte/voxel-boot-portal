package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "第三方实名回调")
public class RealNameCaseCallbackParam {
    @NotBlank private String caseId;
    private String providerOrderNo;
    private Integer success;
    private String message;
}

package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "三方初始化结果")
public class RealNameCaseInitResult {
    private String caseId;
    private String provider;
    private String providerOrderNo;
    private String redirectUrl;
}

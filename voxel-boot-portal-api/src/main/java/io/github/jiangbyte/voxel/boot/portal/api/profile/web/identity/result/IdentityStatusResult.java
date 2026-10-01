package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "实名状态")
public class IdentityStatusResult {
    private String status;
    private String documentType;
    private String realNameMasked;
    private String documentNoMasked;
    private String verifyChannel;
    private String provider;
    private LocalDateTime verifiedAt;
    private LocalDateTime revokedAt;
    private RealNameCaseSummaryResult pendingCase;
}

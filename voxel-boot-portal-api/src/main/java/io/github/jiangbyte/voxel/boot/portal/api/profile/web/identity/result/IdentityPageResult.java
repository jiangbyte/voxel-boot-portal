package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "实名快照分页行")
public class IdentityPageResult {
    private String accountId;
    private String status;
    private String documentType;
    private String realNameMasked;
    private String documentNoMasked;
    private String verifyChannel;
    private String provider;
    private LocalDateTime verifiedAt;
    private LocalDateTime revokedAt;
}

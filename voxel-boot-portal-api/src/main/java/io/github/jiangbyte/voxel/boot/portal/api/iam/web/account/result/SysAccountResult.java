package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "SysAccountResult")
@Data
public class SysAccountResult {
    private String id;
    private String accountType;
    private String accountStatus;
    private String primaryIdentifier;
    private String latestLoginIp;
    private LocalDateTime latestLoginTime;
    private String latestLoginDevice;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SysAccountIdentityResult> identities;
}

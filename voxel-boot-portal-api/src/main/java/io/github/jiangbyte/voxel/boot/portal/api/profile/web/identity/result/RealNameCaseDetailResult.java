package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "实名工单详情")
public class RealNameCaseDetailResult {
    private String caseId;
    private String accountId;
    private String businessType;
    private String verifyChannel;
    private String status;
    private String documentType;
    private String realNameMasked;
    private String documentNoMasked;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String rejectReason;
    private String provider;
    private String providerOrderNo;
    private String submitterId;
    private String reviewerId;
    private List<RealNameCaseAttachmentResult> attachments = new ArrayList<>();
}

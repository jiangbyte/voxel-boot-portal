package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实名工单详情视图。
 *
 * Author: Charlie
 */
public record RealNameCaseDetailView(
        String caseId,
        String accountId,
        String businessType,
        String verifyChannel,
        String status,
        String documentType,
        String realNameMasked,
        String documentNoMasked,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        String rejectReason,
        String provider,
        String providerOrderNo,
        String submitterId,
        String reviewerId,
        List<RealNameCaseAttachmentView> attachments
) {
}

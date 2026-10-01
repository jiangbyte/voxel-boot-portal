package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import java.time.LocalDateTime;

/**
 * 实名工单摘要视图。
 *
 * Author: Charlie
 */
public record RealNameCaseSummaryView(
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
        String rejectReason
) {
}

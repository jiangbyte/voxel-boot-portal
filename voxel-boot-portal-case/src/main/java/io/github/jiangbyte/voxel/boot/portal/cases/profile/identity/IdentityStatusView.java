package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import java.time.LocalDateTime;

/**
 * 实名状态视图。
 *
 * Author: Charlie
 */
public record IdentityStatusView(
        String status,
        String documentType,
        String realNameMasked,
        String documentNoMasked,
        String verifyChannel,
        String provider,
        LocalDateTime verifiedAt,
        LocalDateTime revokedAt,
        RealNameCaseSummaryView pendingCase
) {
}

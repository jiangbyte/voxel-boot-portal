package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import java.time.LocalDateTime;

/**
 * 实名快照分页行。
 *
 * Author: Charlie
 */
public record IdentityPageView(
        String accountId,
        String status,
        String documentType,
        String realNameMasked,
        String documentNoMasked,
        String verifyChannel,
        String provider,
        LocalDateTime verifiedAt,
        LocalDateTime revokedAt
) {
}

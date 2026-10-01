package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 账号只读视图。
 *
 * Author: Charlie
 */
public record AccountView(
        String id,
        String accountType,
        String accountStatus,
        String primaryIdentifier,
        String latestLoginIp,
        LocalDateTime latestLoginTime,
        String latestLoginDevice,
        LocalDateTime cancelledAt,
        String cancelledBy,
        String cancelReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AccountIdentityView> identities
) {
}

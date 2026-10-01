package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

/**
 * 注销账号命令。
 *
 * Author: Charlie
 */
public record CancelAccountCommand(
        String accountId,
        String cancelledBy,
        String cancelReason
) {
}

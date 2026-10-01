package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

/**
 * 管理端更新账号命令。
 *
 * Author: Charlie
 */
public record UpdateAccountCommand(
        String id,
        String account,
        String rawPassword,
        String accountType,
        String accountStatus
) {
}

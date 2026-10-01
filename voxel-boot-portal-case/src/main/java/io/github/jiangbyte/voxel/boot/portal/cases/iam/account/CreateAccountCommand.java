package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

/**
 * 管理端创建账号命令。
 *
 * Author: Charlie
 */
public record CreateAccountCommand(
        String account,
        String rawPassword,
        String accountType,
        String accountStatus,
        String email
) {
}

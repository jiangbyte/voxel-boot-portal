package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port;

/**
 * 账号分页查询条件。
 *
 * Author: Charlie
 */
public record AccountPageQuery(
        long current,
        long size,
        String account,
        String accountType,
        String accountStatus
) {
}

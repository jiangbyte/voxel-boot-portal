package io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port;

/**
 * 账号身份只读视图。
 *
 * Author: Charlie
 */
public record AccountIdentityView(
        String id,
        String identityType,
        String identifier,
        Integer verified,
        Integer isPrimary,
        String bindStatus
) {
}

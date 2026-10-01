package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

/**
 * 门户当前用户摘要。
 *
 * Author: Charlie
 */
public record PortalMeView(
        String accountId,
        String nickname,
        String avatar,
        PortalProfileView profile
) {
}

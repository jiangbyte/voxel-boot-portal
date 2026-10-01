package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

/**
 * 门户资料只读视图。
 *
 * Author: Charlie
 */
public record PortalProfileView(
        String accountId,
        String nickname,
        String avatar,
        String signature,
        String phone,
        String email
) {
}

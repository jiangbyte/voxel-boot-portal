package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

/**
 * 门户公开资料视图。
 *
 * Author: Charlie
 */
public record PublicProfileView(
        String accountId,
        String nickname,
        String account,
        String avatar,
        String signature
) {
}

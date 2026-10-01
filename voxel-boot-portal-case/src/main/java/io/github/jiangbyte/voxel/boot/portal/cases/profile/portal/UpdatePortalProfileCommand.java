package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

/**
 * 更新门户资料命令。
 *
 * Author: Charlie
 */
public record UpdatePortalProfileCommand(
        String nickname,
        String avatar,
        String signature
) {
}

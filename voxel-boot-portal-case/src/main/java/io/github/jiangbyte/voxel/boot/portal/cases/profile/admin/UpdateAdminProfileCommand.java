package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

/**
 * 更新当前用户资料命令。
 *
 * Author: Charlie
 */
public record UpdateAdminProfileCommand(
        String nickname,
        String avatar,
        String signature,
        String remark
) {
}

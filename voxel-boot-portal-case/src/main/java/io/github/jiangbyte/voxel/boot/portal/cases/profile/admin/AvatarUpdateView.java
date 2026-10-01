package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

/**
 * 管理端头像上传结果视图。
 *
 * Author: Charlie
 */
public record AvatarUpdateView(
        String avatar,
        String fileId,
        String objectName,
        String url
) {
}

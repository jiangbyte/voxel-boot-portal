package io.github.jiangbyte.voxel.boot.portal.cases.profile.portal;

/**
 * 头像上传结果视图（门户/管理端共用结构）。
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

package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

/**
 * 管理端资料只读视图。
 *
 * Author: Charlie
 */
public record AdminProfileView(
        String accountId,
        String nickname,
        String avatar,
        String signature,
        String phone,
        String email,
        String remark
) {
}

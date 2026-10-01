package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

/**
 * 当前登录用户摘要（基础版）。
 *
 * Author: Charlie
 */
public record AdminMeView(
        String accountId,
        String nickname,
        String avatar,
        AdminProfileView profile
) {
}

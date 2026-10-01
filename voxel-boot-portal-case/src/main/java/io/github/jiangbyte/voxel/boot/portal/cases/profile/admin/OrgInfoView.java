package io.github.jiangbyte.voxel.boot.portal.cases.profile.admin;

import java.util.List;

/**
 * 管理端组织信息视图。
 *
 * Author: Charlie
 */
public record OrgInfoView(
        List<IdNameView> roleIdNames,
        List<IdNameView> deptIdNames,
        List<IdNameView> groupIdNames
) {
    public record IdNameView(String id, String name) {
    }
}

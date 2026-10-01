package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.grant;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.grant.SysDeptGrantItemParam;
import io.github.jiangbyte.voxel.boot.portal.api.iam.web.grant.SysResourceGrantItemParam;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.DeptGrantItem;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantItem;

import java.util.List;

/**
 * 授权入参映射。
 *
 * Author: Charlie
 */
public final class GrantParamMapper {

    private GrantParamMapper() {
    }

    public static List<ResourceGrantItem> toResourceGrants(List<SysResourceGrantItemParam> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(item -> new ResourceGrantItem(item.getResourceId(), item.getPermissionKeys()))
                .toList();
    }

    public static List<DeptGrantItem> toDeptGrants(List<SysDeptGrantItemParam> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(item -> new DeptGrantItem(item.getDeptId(), item.getIsPrimary()))
                .toList();
    }
}

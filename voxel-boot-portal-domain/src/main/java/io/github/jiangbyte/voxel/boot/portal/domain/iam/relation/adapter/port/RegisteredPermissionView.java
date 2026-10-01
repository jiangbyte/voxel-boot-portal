package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

public record RegisteredPermissionView(
        String permissionKey, String name, String moduleCode, String resourceCode, String action) {
}

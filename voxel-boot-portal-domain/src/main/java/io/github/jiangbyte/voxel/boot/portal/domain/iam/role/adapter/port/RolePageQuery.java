package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port;

/**
 * 角色分页查询。
 *
 * Author: Charlie
 */
public record RolePageQuery(
        long current,
        long size,
        String code,
        String name,
        String category,
        String scopeType,
        String status
) {
}

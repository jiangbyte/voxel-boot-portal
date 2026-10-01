package io.github.jiangbyte.voxel.boot.portal.cases.iam.role;

/**
 * 创建角色命令。
 *
 * Author: Charlie
 */
public record CreateRoleCommand(
        String code,
        String name,
        String category,
        String scopeType,
        String ownerDeptId,
        Integer sort,
        String status,
        Integer builtin,
        String description
) {
}

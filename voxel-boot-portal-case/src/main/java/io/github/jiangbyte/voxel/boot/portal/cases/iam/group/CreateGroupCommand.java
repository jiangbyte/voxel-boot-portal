package io.github.jiangbyte.voxel.boot.portal.cases.iam.group;

/**
 * 创建用户组命令。
 *
 * Author: Charlie
 */
public record CreateGroupCommand(
        String name,
        String ownerDeptId,
        String description,
        String status,
        String extra
) {
}

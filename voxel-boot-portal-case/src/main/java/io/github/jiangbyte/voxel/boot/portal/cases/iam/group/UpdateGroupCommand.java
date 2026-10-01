package io.github.jiangbyte.voxel.boot.portal.cases.iam.group;

/**
 * 更新用户组命令。
 *
 * Author: Charlie
 */
public record UpdateGroupCommand(
        String id,
        String name,
        String ownerDeptId,
        String description,
        String status,
        String extra
) {
}

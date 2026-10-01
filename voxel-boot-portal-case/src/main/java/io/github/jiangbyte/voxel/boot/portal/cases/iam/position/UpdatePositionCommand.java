package io.github.jiangbyte.voxel.boot.portal.cases.iam.position;

/**
 * 更新岗位命令。
 *
 * Author: Charlie
 */
public record UpdatePositionCommand(
        String id,
        String name,
        String category,
        String ownerDeptId,
        Integer sort,
        Integer virtual,
        String status,
        String description,
        String extra
) {
}

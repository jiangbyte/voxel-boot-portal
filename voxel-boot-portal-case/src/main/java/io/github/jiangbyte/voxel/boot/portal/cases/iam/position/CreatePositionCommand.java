package io.github.jiangbyte.voxel.boot.portal.cases.iam.position;

/**
 * 创建岗位命令。
 *
 * Author: Charlie
 */
public record CreatePositionCommand(
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

package io.github.jiangbyte.voxel.boot.portal.cases.iam.dept;

/**
 * 创建部门命令。
 *
 * Author: Charlie
 */
public record CreateDeptCommand(
        String parentId,
        String masterId,
        String deputyMasterId,
        String name,
        String category,
        Integer sort,
        Integer virtual,
        String status,
        String extra
) {
}

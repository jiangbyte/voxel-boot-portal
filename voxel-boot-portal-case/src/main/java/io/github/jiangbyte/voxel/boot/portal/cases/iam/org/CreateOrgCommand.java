package io.github.jiangbyte.voxel.boot.portal.cases.iam.org;

/**
 * 创建组织命令。
 *
 * Author: Charlie
 */
public record CreateOrgCommand(
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

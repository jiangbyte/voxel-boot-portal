package io.github.jiangbyte.voxel.boot.portal.cases.iam.org;

/**
 * 更新组织命令。
 *
 * Author: Charlie
 */
public record UpdateOrgCommand(
        String id,
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

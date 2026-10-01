package io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port;

/**
 * 用户组分页查询。
 *
 * Author: Charlie
 */
public record GroupPageQuery(
        long current,
        long size,
        String name,
        String ownerDeptId,
        String status
) {
}

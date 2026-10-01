package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port;

/**
 * 组织分页查询。
 *
 * Author: Charlie
 */
public record OrgPageQuery(
        long current,
        long size,
        String name,
        String category,
        String status,
        String parentId
) {
}

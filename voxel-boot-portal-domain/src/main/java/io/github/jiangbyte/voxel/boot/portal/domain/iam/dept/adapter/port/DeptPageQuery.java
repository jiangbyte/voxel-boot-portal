package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port;

/**
 * 部门分页查询。
 *
 * Author: Charlie
 */
public record DeptPageQuery(
        long current,
        long size,
        String name,
        String category,
        String status,
        String parentId
) {
}

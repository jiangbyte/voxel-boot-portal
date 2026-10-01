package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port;

/**
 * 岗位分页查询。
 *
 * Author: Charlie
 */
public record PositionPageQuery(
        long current,
        long size,
        String name,
        String category,
        String status
) {
}

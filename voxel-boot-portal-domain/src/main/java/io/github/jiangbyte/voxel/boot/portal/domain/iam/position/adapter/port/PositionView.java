package io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port;

import java.time.LocalDateTime;

/**
 * 岗位只读视图。
 *
 * Author: Charlie
 */
public record PositionView(
        String id,
        String name,
        String category,
        String ownerDeptId,
        Integer sort,
        Integer virtual,
        String status,
        String description,
        String extra,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

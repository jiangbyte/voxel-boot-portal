package io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port;

import java.time.LocalDateTime;

/**
 * 用户组只读视图。
 *
 * Author: Charlie
 */
public record GroupView(
        String id,
        String name,
        String ownerDeptId,
        String description,
        String status,
        String extra,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

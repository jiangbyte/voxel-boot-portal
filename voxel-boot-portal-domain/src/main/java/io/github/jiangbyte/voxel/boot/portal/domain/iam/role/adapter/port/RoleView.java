package io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port;

import java.time.LocalDateTime;

/**
 * 角色只读视图。
 *
 * Author: Charlie
 */
public record RoleView(
        String id,
        String code,
        String name,
        String category,
        String scopeType,
        String ownerDeptId,
        Integer sort,
        String status,
        Integer builtin,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

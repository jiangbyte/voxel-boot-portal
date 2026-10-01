package io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port;

import java.time.LocalDateTime;

/**
 * 部门只读视图。
 *
 * Author: Charlie
 */
public record DeptView(
        String id,
        String parentId,
        String masterId,
        String deputyMasterId,
        String name,
        String category,
        Integer sort,
        Integer virtual,
        String status,
        String extra,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

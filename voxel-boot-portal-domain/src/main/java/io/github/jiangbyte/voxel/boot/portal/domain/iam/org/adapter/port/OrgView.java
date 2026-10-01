package io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port;

import java.time.LocalDateTime;

/**
 * 组织只读视图。
 *
 * Author: Charlie
 */
public record OrgView(
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

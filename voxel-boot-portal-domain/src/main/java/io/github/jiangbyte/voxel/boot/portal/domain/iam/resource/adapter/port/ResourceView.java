package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port;

import java.time.LocalDateTime;

/**
 * 资源只读视图。
 *
 * Author: Charlie
 */
public record ResourceView(
        String id,
        String parentId,
        String code,
        String name,
        String resourceType,
        String moduleId,
        String path,
        String component,
        String redirect,
        String icon,
        String color,
        String href,
        Integer sort,
        Integer visible,
        Integer cache,
        Integer affix,
        String status,
        String description,
        String layout,
        String extra,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

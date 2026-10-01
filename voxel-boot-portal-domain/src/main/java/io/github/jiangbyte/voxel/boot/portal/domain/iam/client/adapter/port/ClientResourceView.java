package io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port;

import java.time.LocalDateTime;
import java.util.Map;

public record ClientResourceView(
        String id, String parentId, String code, String name, String resourceType, String moduleId,
        String path, String component, String redirect, String icon, String color, String href,
        Integer sort, Integer isVisible, Integer isCache, Integer isAffix, String status,
        String description, String layout, Map<String, Object> extra,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
}

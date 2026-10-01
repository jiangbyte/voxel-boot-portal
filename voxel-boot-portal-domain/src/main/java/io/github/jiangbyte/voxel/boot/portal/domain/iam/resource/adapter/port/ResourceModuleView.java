package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port;

import java.time.LocalDateTime;
import java.util.Map;

public record ResourceModuleView(
        String id, String name, String code, String client, String icon, String color,
        Integer sort, String status, String description, Map<String, Object> extra,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
}

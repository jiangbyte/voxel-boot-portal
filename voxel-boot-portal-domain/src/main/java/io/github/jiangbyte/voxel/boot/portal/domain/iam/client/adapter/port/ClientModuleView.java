package io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port;

import java.time.LocalDateTime;
import java.util.Map;

public record ClientModuleView(
        String id, String name, String code, String accountType, String icon, String color,
        Integer sort, String status, String description, Map<String, Object> extra,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
}

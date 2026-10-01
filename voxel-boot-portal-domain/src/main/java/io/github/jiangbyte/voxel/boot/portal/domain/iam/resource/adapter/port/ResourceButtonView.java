package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port;

import java.time.LocalDateTime;
import java.util.List;

public record ResourceButtonView(
        String id, String parentId, String code, String name, String status, Integer sort,
        String description, String permissionKey, String dataScope, List<String> customScopeDeptIds,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port;

/**
 * 资源分页查询。
 *
 * Author: Charlie
 */
public record ResourcePageQuery(
        long current,
        long size,
        String code,
        String name,
        String resourceType,
        String moduleId,
        String status
) {
}

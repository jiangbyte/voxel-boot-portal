package io.github.jiangbyte.voxel.boot.portal.cases.iam.resource;

/**
 * 更新资源命令。
 *
 * Author: Charlie
 */
public record UpdateResourceCommand(
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
        String extra
) {
}

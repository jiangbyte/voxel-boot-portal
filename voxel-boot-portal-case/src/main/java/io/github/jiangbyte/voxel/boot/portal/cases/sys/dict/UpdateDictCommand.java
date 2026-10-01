package io.github.jiangbyte.voxel.boot.portal.cases.sys.dict;

/**
 * 更新字典命令。
 *
 * Author: Charlie
 */
public record UpdateDictCommand(
        String id,
        String code,
        String label,
        String value,
        String color,
        String category,
        String parentId,
        String status,
        Integer sort
) {
}

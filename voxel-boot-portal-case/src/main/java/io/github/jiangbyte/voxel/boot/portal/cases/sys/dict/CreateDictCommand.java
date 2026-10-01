package io.github.jiangbyte.voxel.boot.portal.cases.sys.dict;

/**
 * 创建字典命令。
 *
 * Author: Charlie
 */
public record CreateDictCommand(
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

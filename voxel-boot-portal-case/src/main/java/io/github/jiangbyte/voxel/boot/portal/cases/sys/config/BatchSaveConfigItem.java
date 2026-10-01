package io.github.jiangbyte.voxel.boot.portal.cases.sys.config;

/**
 * 批量保存单项。
 *
 * Author: Charlie
 */
public record BatchSaveConfigItem(
        String configKey,
        String configValue,
        String category,
        String valueType,
        String label,
        String remark,
        Integer sortCode,
        Integer version
) {
}

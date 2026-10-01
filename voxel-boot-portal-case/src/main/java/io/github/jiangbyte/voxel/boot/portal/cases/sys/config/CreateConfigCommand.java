package io.github.jiangbyte.voxel.boot.portal.cases.sys.config;

/**
 * 创建系统配置命令。
 *
 * Author: Charlie
 */
public record CreateConfigCommand(
        String configKey,
        String configValue,
        String category,
        String remark,
        Integer sortCode,
        String valueType,
        String label,
        String scope,
        String scene,
        Integer builtin,
        String extJson
) {
}

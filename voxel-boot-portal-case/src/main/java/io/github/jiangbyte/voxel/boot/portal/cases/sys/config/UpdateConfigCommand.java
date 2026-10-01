package io.github.jiangbyte.voxel.boot.portal.cases.sys.config;

/**
 * 更新系统配置命令。
 *
 * Author: Charlie
 */
public record UpdateConfigCommand(
        String id,
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

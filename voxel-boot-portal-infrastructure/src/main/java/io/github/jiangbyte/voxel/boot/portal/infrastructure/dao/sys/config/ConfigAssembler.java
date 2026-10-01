package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.Config;
import org.springframework.util.StringUtils;

/**
 * 系统配置聚合与持久化对象互转：对外统一 config_value，落库按类型拆列。
 *
 * Author: Charlie
 */
public final class ConfigAssembler {

    private ConfigAssembler() {
    }

    /**
     * 将 DB 列合并为对外统一的配置值字符串。
     */
    public static String externalValue(ConfigPO po) {
        if (po == null) {
            return null;
        }
        String type = po.getValueType() == null ? "" : po.getValueType().trim().toUpperCase();
        if ("JSON".equals(type) && StringUtils.hasText(po.getConfigJson())) {
            return po.getConfigJson();
        }
        return po.getConfigValue();
    }

    /**
     * 按 value_type 将统一配置值拆分到 config_value / config_json。
     */
    public static void applyExternalValue(ConfigPO po, String external, String valueType) {
        String type = valueType == null || valueType.isBlank() ? "STRING" : valueType.trim().toUpperCase();
        if ("INT".equals(type) || "INTEGER".equals(type) || "LONG".equals(type)) {
            type = "NUMBER";
        }
        po.setValueType(type);
        if ("JSON".equals(type)) {
            po.setConfigJson(StringUtils.hasText(external) ? external.trim() : null);
            po.setConfigValue(null);
            return;
        }
        po.setConfigValue(external);
        po.setConfigJson(null);
    }

    /**
     * 持久化对象转聚合（调用方负责解密敏感值）。
     */
    public static Config toAggregate(ConfigPO po) {
        if (po == null) {
            return null;
        }
        return Config.reconstitute(
                po.getId(),
                po.getConfigKey(),
                externalValue(po),
                po.getCategory(),
                po.getRemark(),
                po.getSortCode(),
                po.getValueType(),
                po.getLabel(),
                po.getScope(),
                po.getScene(),
                po.getIsBuiltin(),
                po.getExtJson(),
                po.getVersion() == null ? 0 : po.getVersion());
    }

    /**
     * 聚合转持久化对象（调用方负责加密敏感标量后再拆列，或在此直接拆列明文）。
     */
    public static ConfigPO toPO(Config config) {
        if (config == null) {
            return null;
        }
        ConfigPO po = new ConfigPO();
        po.setId(config.getId());
        po.setConfigKey(config.getConfigKey());
        applyExternalValue(po, config.getConfigValue(), config.getValueType());
        po.setCategory(config.getCategory());
        po.setRemark(config.getRemark());
        po.setSortCode(config.getSortCode());
        po.setLabel(config.getLabel());
        po.setScope(config.getScope());
        po.setScene(config.getScene());
        po.setIsBuiltin(config.getBuiltin());
        po.setExtJson(config.getExtJson());
        po.setVersion(config.getVersion() == null ? 0 : config.getVersion());
        return po;
    }
}

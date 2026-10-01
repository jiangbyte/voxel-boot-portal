package io.github.jiangbyte.voxel.boot.portal.domain.sys.config;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.event.ConfigCreated;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.event.ConfigDeleted;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.event.ConfigUpdated;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 系统配置聚合根：维护配置键唯一性、值类型与内置/敏感等不变量。
 *
 * Author: Charlie
 */
public class Config extends AggregateRoot<String> {

    private static final Set<String> VALUE_TYPES = Set.of("STRING", "JSON", "NUMBER");
    private static final Set<String> SCOPES = Set.of("GLOBAL", "ADMIN", "PORTAL");
    /** 敏感配置键（与 Gin/FastAPI SENSITIVE_CONFIG_KEYS 对齐）。 */
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "AUTH_DEFAULT_PASSWORD",
            "AUTH_JWT_SECRET",
            "AUDIT_ALERT_WEBHOOK_SECRET",
            "MAIL_LOCAL_PASSWORD",
            "MAIL_ALIYUN_ACCESS_KEY_SECRET",
            "MAIL_TENCENT_SECRET_KEY",
            "SMS_ALIYUN_ACCESS_KEY_SECRET",
            "SMS_TENCENT_SECRET_KEY",
            "PUSH_DINGTALK_SECRET",
            "PUSH_LARK_SECRET",
            "STORAGE_MINIO_ACCESS_KEY",
            "STORAGE_MINIO_SECRET_KEY",
            "STORAGE_RUSTFS_ACCESS_KEY",
            "STORAGE_RUSTFS_SECRET_KEY",
            "STORAGE_ALIYUN_ACCESS_KEY",
            "STORAGE_ALIYUN_SECRET_KEY",
            "STORAGE_TENCENT_ACCESS_KEY",
            "STORAGE_TENCENT_SECRET_KEY",
            "NOTIFY_SMS_ACCESS_KEY_SECRET",
            "NOTIFY_EMAIL_PASSWORD",
            "STORAGE_S3_SECRET_KEY");

    private String id;
    private String configKey;
    private String configValue;
    private String category;
    private String remark;
    private Integer sortCode;
    private String valueType;
    private String label;
    private String scope;
    private String scene;
    private Integer builtin;
    private String extJson;
    private Integer version;

    private Config() {
    }

    /**
     * 新建配置项并登记创建事件。
     */
    public static Config create(String configKey,
                                String configValue,
                                String category,
                                String remark,
                                Integer sortCode,
                                String valueType,
                                String label,
                                String scope,
                                String scene,
                                Integer builtin,
                                String extJson) {
        // 1. 校验配置键与值类型/作用域白名单
        String key = requireKey(configKey);
        String type = normalizeValueType(valueType);
        String normalizedScope = normalizeScope(scope);
        // 2. 组装聚合并分配标识
        Config config = new Config();
        config.id = UUID.randomUUID().toString().replace("-", "");
        config.configKey = key;
        config.configValue = blankToEmpty(configValue);
        config.category = (category == null || category.isBlank()) ? "SYS" : category.trim();
        config.remark = remark;
        config.sortCode = sortCode == null ? 0 : sortCode;
        config.valueType = type;
        config.label = label;
        config.scope = normalizedScope;
        config.scene = blankToNull(scene);
        config.builtin = Flags.of(builtin);
        config.extJson = (extJson == null || extJson.isBlank()) ? "{}" : extJson.trim();
        config.version = 0;
        // 3. 登记领域事件
        config.registerEvent(new ConfigCreated(config.id, config.configKey));
        return config;
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static Config reconstitute(String id,
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
                                      String extJson,
                                      Integer version) {
        Config config = new Config();
        config.id = id;
        config.configKey = configKey;
        config.configValue = configValue;
        config.category = category;
        config.remark = remark;
        config.sortCode = sortCode == null ? 0 : sortCode;
        config.valueType = normalizeValueType(valueType);
        config.label = label;
        config.scope = normalizeScope(scope);
        config.scene = scene;
        config.builtin = Flags.of(builtin);
        config.extJson = extJson;
        config.version = version == null ? 0 : version;
        return config;
    }

    /**
     * 更新配置属性并登记更新事件。
     */
    public void update(String configKey,
                       String configValue,
                       String category,
                       String remark,
                       Integer sortCode,
                       String valueType,
                       String label,
                       String scope,
                       String scene,
                       Integer builtin,
                       String extJson) {
        // 1. 校验键与类型
        this.configKey = requireKey(configKey);
        this.valueType = normalizeValueType(valueType);
        this.scope = normalizeScope(scope);
        // 2. 覆盖可变字段
        this.configValue = blankToEmpty(configValue);
        this.category = (category == null || category.isBlank()) ? "SYS" : category.trim();
        this.remark = remark;
        this.sortCode = sortCode == null ? 0 : sortCode;
        this.label = label;
        this.scene = blankToNull(scene);
        this.builtin = Flags.of(builtin);
        this.extJson = (extJson == null || extJson.isBlank()) ? "{}" : extJson.trim();
        // 3. 登记更新事件
        registerEvent(new ConfigUpdated(this.id, this.configKey));
    }

    /**
     * 仅更新配置值（批量保存场景）。
     */
    public void changeValue(String configValue, String valueType) {
        // 1. 可选覆盖值类型
        if (valueType != null && !valueType.isBlank()) {
            this.valueType = normalizeValueType(valueType);
        }
        // 2. 写入新值
        this.configValue = blankToEmpty(configValue);
        // 3. 登记更新事件
        registerEvent(new ConfigUpdated(this.id, this.configKey));
    }

    /**
     * 校验客户端提交的乐观锁版本；不一致则抛出可识别冲突。
     */
    public void assertVersion(Integer expected) {
        if (expected == null) {
            return;
        }
        int current = this.version == null ? 0 : this.version;
        if (expected != current) {
            throw new DomainException(409, "配置已被他人修改，请刷新后重试");
        }
    }

    /**
     * 持久化成功后同步本地版本号。
     */
    public void syncVersion(int version) {
        this.version = version;
    }

    /**
     * 标记删除；内置配置禁止删除。
     */
    public void markDeleted() {
        // 1. 内置项不可删
        if (java.util.Objects.equals(builtin, 1)) {
            throw new DomainException("Builtin config cannot be deleted");
        }
        // 2. 登记删除事件
        registerEvent(new ConfigDeleted(this.id, this.configKey));
    }

    /**
     * 判断当前配置键是否敏感（密钥类，读侧需脱敏）。
     */
    public Integer isSensitive() {
        return Flags.of(isSensitiveKey(this.configKey));
    }

    /**
     * 按配置键判断是否敏感。
     */
    public static boolean isSensitiveKey(String configKey) {
        if (configKey == null || configKey.isBlank()) {
            return false;
        }
        String key = configKey.trim();
        if (SENSITIVE_KEYS.contains(key)) {
            return true;
        }
        String up = key.toUpperCase(Locale.ROOT);
        return up.startsWith("AUTH_OAUTH_")
                && (up.endsWith("_CLIENT_SECRET") || up.endsWith("_APP_SECRET"));
    }

    private static String requireKey(String configKey) {
        if (configKey == null || configKey.isBlank()) {
            throw new DomainException("Config key must not be blank");
        }
        return configKey.trim();
    }

    private static String normalizeValueType(String valueType) {
        // 1. 缺省 STRING；库中历史别名 INT/BOOL 归一为 NUMBER（与种子数据兼容）
        String type = valueType == null || valueType.isBlank() ? "STRING" : valueType.trim().toUpperCase(Locale.ROOT);
        if ("INT".equals(type) || "INTEGER".equals(type) || "LONG".equals(type) || "BOOL".equals(type)) {
            type = "NUMBER";
        }
        // 2. 白名单校验
        if (!VALUE_TYPES.contains(type)) {
            throw new DomainException("Config valueType must be STRING/JSON/NUMBER");
        }
        return type;
    }

    private static String normalizeScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return "GLOBAL";
        }
        String normalized = scope.trim().toUpperCase(Locale.ROOT);
        if (!SCOPES.contains(normalized)) {
            throw new DomainException("Config scope must be GLOBAL/ADMIN/PORTAL");
        }
        return normalized;
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    private static String blankToEmpty(String text) {
        return text == null ? "" : text;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public String getCategory() {
        return category;
    }

    public String getRemark() {
        return remark;
    }

    public Integer getSortCode() {
        return sortCode;
    }

    public String getValueType() {
        return valueType;
    }

    public String getLabel() {
        return label;
    }

    public String getScope() {
        return scope;
    }

    public String getScene() {
        return scene;
    }

    public Integer getBuiltin() {
        return builtin;
    }

    public String getExtJson() {
        return extJson;
    }

    public Integer getVersion() {
        return version;
    }
}

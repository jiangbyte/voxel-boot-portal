package io.github.jiangbyte.voxel.boot.portal.cases.sys.config;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.Config;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.repository.ConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 系统配置写用例：创建、更新、删除、批量保存，并实现 {@link ConfigApi}。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ConfigCaseImpl implements IConfigCase, ConfigApi {

    private final ConfigRepository configRepository;


    /**
     * 创建配置项。
     */
    @Transactional
    public void create(CreateConfigCommand command) {
        // 1. 校验配置键唯一
        ensureKeyUnique(command.configKey(), null);
        // 2. 工厂创建聚合
        Config config = Config.create(
                command.configKey(),
                command.configValue(),
                command.category(),
                command.remark(),
                command.sortCode(),
                command.valueType(),
                command.label(),
                command.scope(),
                command.scene(),
                command.builtin(),
                command.extJson());
        // 3. 持久化（仓储内加密拆列）
        configRepository.save(config);
    }

    /**
     * 更新配置项。
     */
    @Transactional
    public void update(UpdateConfigCommand command) {
        // 1. 加载聚合
        Config config = configRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "Config not found"));
        // 2. 校验键未被占用
        ensureKeyUnique(command.configKey(), command.id());
        // 3. 聚合内更新并保存
        config.update(
                command.configKey(),
                command.configValue(),
                command.category(),
                command.remark(),
                command.sortCode(),
                command.valueType(),
                command.label(),
                command.scope(),
                command.scene(),
                command.builtin(),
                command.extJson());
        configRepository.save(config);
    }

    /**
     * 批量删除。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载、校验内置后删除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            configRepository.findById(id).ifPresent(config -> {
                config.markDeleted();
                configRepository.remove(config);
            });
        }
    }

    /**
     * 按键批量 upsert 配置值。
     */
    @Transactional
    public void batchSave(List<BatchSaveConfigItem> items) {
        // 1. 空列表直接返回
        if (items == null || items.isEmpty()) {
            return;
        }
        // 2. 逐项：存在则改值，不存在则创建
        for (BatchSaveConfigItem item : items) {
            if (item == null || item.configKey() == null || item.configKey().isBlank()) {
                continue;
            }
            // 敏感键空值表示「未改动」，跳过
            if (Config.isSensitiveKey(item.configKey())
                    && (item.configValue() == null || item.configValue().isBlank())) {
                continue;
            }
            configRepository.findByKey(item.configKey()).ifPresentOrElse(existing -> {
                // 可选乐观锁：客户端带 version 时校验
                existing.assertVersion(item.version());
                existing.changeValue(item.configValue(), item.valueType());
                configRepository.save(existing);
            }, () -> {
                Config created = Config.create(
                        item.configKey(),
                        item.configValue(),
                        item.category(),
                        item.remark(),
                        item.sortCode(),
                        item.valueType(),
                        item.label(),
                        "GLOBAL",
                        null,
                        0,
                        null);
                configRepository.save(created);
            });
        }
    }

    @Override
    public String getByKey(String key) {
        // 1. 空键返回 null
        if (key == null || key.isBlank()) {
            return null;
        }
        // 2. 按键加载并返回已解密的对外值
        return configRepository.findByKey(key)
                .map(Config::getConfigValue)
                .orElse(null);
    }

    @Override
    public String getByKey(String key, String defaultValue) {
        // 1. 读取配置值
        String value = getByKey(key);
        // 2. 空白则回退默认值
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private void ensureKeyUnique(String configKey, String excludeId) {
        configRepository.findByKey(configKey).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new DomainException("Config key already exists");
            }
        });
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.Config;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.repository.ConfigRepository;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config.ConfigAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config.ConfigCryptoService;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config.ConfigMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config.ConfigPO;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * 系统配置仓储实现：拆列存、拼列出、敏感加密与乐观锁。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class ConfigRepositoryImpl implements ConfigRepository {

    private final ConfigMapper configMapper;
    private final DomainEventPublisher domainEventPublisher;
    private final ConfigCryptoService configCryptoService;


    @Override
    public Optional<Config> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(loadAggregate(configMapper.selectById(id)));
    }

    @Override
    public Optional<Config> findByKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return Optional.empty();
        }
        ConfigPO po = configMapper.selectOne(Wrappers.<ConfigPO>lambdaQuery()
                .eq(ConfigPO::getConfigKey, configKey)
                .last("LIMIT 1"));
        return Optional.ofNullable(loadAggregate(po));
    }

    @Override
    public void save(Config aggregate) {
        // 1. 聚合转 PO，并对敏感标量加密后拆列
        ConfigPO po = toEncryptedPO(aggregate);
        // 2. 按主键判断插入或带版本条件更新（显式 set 允许 null 清空对侧列）
        ConfigPO existing = configMapper.selectById(po.getId());
        if (existing == null) {
            po.setVersion(0);
            configMapper.insert(po);
            aggregate.syncVersion(0);
        } else {
            int expected = aggregate.getVersion() == null ? 0 : aggregate.getVersion();
            int next = expected + 1;
            int updated = configMapper.update(
                    null,
                    Wrappers.<ConfigPO>lambdaUpdate()
                            .eq(ConfigPO::getId, po.getId())
                            .eq(ConfigPO::getVersion, expected)
                            .set(ConfigPO::getConfigKey, po.getConfigKey())
                            .set(ConfigPO::getConfigValue, po.getConfigValue())
                            .set(ConfigPO::getConfigJson, po.getConfigJson())
                            .set(ConfigPO::getCategory, po.getCategory())
                            .set(ConfigPO::getRemark, po.getRemark())
                            .set(ConfigPO::getSortCode, po.getSortCode())
                            .set(ConfigPO::getValueType, po.getValueType())
                            .set(ConfigPO::getLabel, po.getLabel())
                            .set(ConfigPO::getScope, po.getScope())
                            .set(ConfigPO::getScene, po.getScene())
                            .set(ConfigPO::getIsBuiltin, po.getIsBuiltin())
                            .set(ConfigPO::getExtJson, po.getExtJson())
                            .set(ConfigPO::getVersion, next));
            if (updated == 0) {
                throw new DomainException(409, "配置已被他人修改，请刷新后重试");
            }
            aggregate.syncVersion(next);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        configMapper.deleteById(id);
    }

    @Override
    public void remove(Config aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        configMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    private Config loadAggregate(ConfigPO po) {
        if (po == null) {
            return null;
        }
        // 1. 拼列得到对外值
        String external = ConfigAssembler.externalValue(po);
        // 2. 敏感密文解密后重建聚合
        String plain = configCryptoService.decryptForRead(external);
        return Config.reconstitute(
                po.getId(),
                po.getConfigKey(),
                plain,
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

    private ConfigPO toEncryptedPO(Config aggregate) {
        // 1. 敏感标量加密，JSON 类型不加密
        String external = aggregate.getConfigValue();
        boolean jsonType = "JSON".equalsIgnoreCase(aggregate.getValueType());
        if (!jsonType && Config.isSensitiveKey(aggregate.getConfigKey())) {
            external = configCryptoService.encryptForWrite(external, true);
        }
        // 2. 组装 PO 并拆列
        ConfigPO po = new ConfigPO();
        po.setId(aggregate.getId());
        po.setConfigKey(aggregate.getConfigKey());
        ConfigAssembler.applyExternalValue(po, external, aggregate.getValueType());
        po.setCategory(aggregate.getCategory());
        po.setRemark(aggregate.getRemark());
        po.setSortCode(aggregate.getSortCode());
        po.setLabel(aggregate.getLabel());
        po.setScope(aggregate.getScope());
        po.setScene(aggregate.getScene());
        po.setIsBuiltin(aggregate.getBuiltin());
        po.setExtJson(aggregate.getExtJson());
        return po;
    }
}

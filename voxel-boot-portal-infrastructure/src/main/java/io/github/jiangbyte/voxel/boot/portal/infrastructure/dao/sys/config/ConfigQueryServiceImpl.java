package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.Config;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigView;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 系统配置只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ConfigQueryServiceImpl implements ConfigQueryService {

    private final ConfigMapper configMapper;
    private final ConfigCryptoService configCryptoService;


    @Override
    public ConfigView detail(String id) {
        // 1. 按主键加载
        ConfigPO po = configMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Config not found");
        }
        // 3. 转为视图（敏感值脱敏）
        return toView(po);
    }

    @Override
    public PageResult<ConfigView> page(ConfigPageQuery query) {
        // 1. 数据库分页查询
        Page<ConfigPO> page = configMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<ConfigPO>lambdaQuery()
                        .like(StringUtils.hasText(query.configKey()), ConfigPO::getConfigKey, query.configKey())
                        .eq(StringUtils.hasText(query.category()), ConfigPO::getCategory, query.category())
                        .orderByAsc(ConfigPO::getSortCode)
                        .orderByDesc(ConfigPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<ConfigView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<ConfigView> list(String category) {
        // 1. 按分类列出（分类可空）
        List<ConfigPO> list = configMapper.selectList(Wrappers.<ConfigPO>lambdaQuery()
                .eq(StringUtils.hasText(category), ConfigPO::getCategory, category)
                .orderByAsc(ConfigPO::getSortCode));
        // 2. 映射视图
        return list.stream().map(this::toView).toList();
    }

    private ConfigView toView(ConfigPO po) {
        // 1. 拼列得到对外值并尝试解密
        String stored = ConfigAssembler.externalValue(po);
        String plain = stored;
        try {
            plain = configCryptoService.decryptForRead(stored);
        } catch (RuntimeException ignored) {
            plain = stored;
        }
        Integer sensitive = Flags.of(Config.isSensitiveKey(po.getConfigKey()));
        // 2. 敏感键不回显明文，仅在 ext_json 标记 is_set
        String displayValue = Flags.isOn(sensitive) ? "" : plain;
        String extJson = po.getExtJson();
        if (Flags.isOn(sensitive) && StringUtils.hasText(stored)) {
            extJson = mergeIsSet(extJson);
        }
        return new ConfigView(
                po.getId(),
                po.getConfigKey(),
                displayValue,
                po.getCategory(),
                po.getRemark(),
                po.getSortCode(),
                po.getValueType(),
                po.getLabel(),
                po.getScope(),
                po.getScene(),
                po.getIsBuiltin(),
                sensitive,
                extJson,
                po.getVersion() == null ? 0 : po.getVersion(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }

    private static String mergeIsSet(String extJson) {
        if (!StringUtils.hasText(extJson) || "{}".equals(extJson.trim())) {
            return "{\"is_set\":true}";
        }
        String trimmed = extJson.trim();
        if (trimmed.contains("\"is_set\"")) {
            return trimmed;
        }
        if (trimmed.endsWith("}")) {
            return trimmed.substring(0, trimmed.length() - 1) + ",\"is_set\":true}";
        }
        return "{\"is_set\":true}";
    }
}

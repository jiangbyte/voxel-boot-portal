package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.dict;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictItem;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.Dict;
import lombok.RequiredArgsConstructor;

/**
 * 字典只读查询实现：Mapper 投影 + 树组装。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class DictQueryServiceImpl implements DictQueryService {

    private final DictMapper dictMapper;


    @Override
    public DictView detail(String id) {
        // 1. 按主键加载
        DictPO po = dictMapper.selectById(id);
        // 2. 不存在则抛领域异常
        if (po == null) {
            throw new DomainException(404, "Dict not found");
        }
        // 3. 转为只读视图
        return toView(po);
    }

    @Override
    public PageResult<DictView> page(DictPageQuery query) {
        // 1. 数据库分页查询（禁止内存截断）
        Page<DictPO> page = dictMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<DictPO>lambdaQuery()
                        .like(StringUtils.hasText(query.code()), DictPO::getCode, query.code())
                        .eq(StringUtils.hasText(query.category()), DictPO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.parentId()), DictPO::getParentId, query.parentId())
                        .eq(StringUtils.hasText(query.status()), DictPO::getStatus, query.status())
                        .orderByAsc(DictPO::getSort)
                        .orderByDesc(DictPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<DictView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Tree<String>> tree(String category) {
        // 1. 按分类拉取全量子集（树构建需要完整父子关系，非列表分页场景）
        List<DictPO> all = dictMapper.selectList(Wrappers.<DictPO>lambdaQuery()
                .eq(StringUtils.hasText(category), DictPO::getCategory, category)
                .orderByAsc(DictPO::getSort));
        if (all.isEmpty()) {
            return List.of();
        }
        // 2. 收集 ID 集合，孤立父引用视为根
        Set<String> ids = all.stream().map(DictPO::getId).collect(Collectors.toSet());
        TreeNodeConfig config = new TreeNodeConfig();
        config.setIdKey("id");
        config.setParentIdKey("parent_id");
        config.setNameKey("name");
        config.setWeightKey("weight");
        config.setChildrenKey("children");
        // 3. Hutool 建树并填充扩展字段
        return TreeUtil.build(all, null, config, (dict, tree) -> {
            String parentId = dict.getParentId();
            if (!StringUtils.hasText(parentId) || !ids.contains(parentId)) {
                parentId = null;
            }
            BeanUtil.beanToMap(dict, false, true).forEach((key, value) -> {
                if (!"children".equals(key)) {
                    tree.putExtra(StrUtil.toUnderlineCase(key), value);
                }
            });
            tree.setId(dict.getId());
            tree.setParentId(parentId);
            tree.setName(dict.getLabel());
            tree.setWeight(dict.getSort() == null ? 0 : dict.getSort());
        });
    }

    @Override
    public List<DictItem> listByType(String dictType) {
        // 1. 仅启用项；按 code 或 category 匹配类型
        List<DictPO> list = dictMapper.selectList(Wrappers.<DictPO>lambdaQuery()
                .eq(DictPO::getStatus, "ENABLED")
                .and(wrapper -> wrapper.eq(DictPO::getCode, dictType).or().eq(DictPO::getCategory, dictType))
                .orderByAsc(DictPO::getSort));
        // 2. 转为跨上下文 DictItem
        return list.stream().map(this::toItem).toList();
    }

    private DictView toView(DictPO po) {
        return new DictView(
                po.getId(),
                po.getCode(),
                po.getLabel(),
                po.getValue(),
                po.getColor(),
                po.getCategory(),
                po.getParentId(),
                po.getStatus(),
                po.getSort(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }

    private DictItem toItem(DictPO po) {
        DictItem item = new DictItem();
        item.setId(po.getId());
        item.setCode(po.getCode());
        item.setLabel(po.getLabel());
        item.setValue(po.getValue());
        item.setColor(po.getColor());
        item.setParentId(po.getParentId());
        item.setSort(po.getSort());
        return item;
    }
}

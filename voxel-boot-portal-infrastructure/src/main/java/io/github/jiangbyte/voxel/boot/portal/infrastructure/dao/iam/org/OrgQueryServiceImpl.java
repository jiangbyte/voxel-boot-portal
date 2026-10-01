package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.org;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port.OrgPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port.OrgQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port.OrgView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.Org;
import lombok.RequiredArgsConstructor;

/**
 * 组织只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class OrgQueryServiceImpl implements OrgQueryService {

    private final OrgMapper orgMapper;


    @Override
    public OrgView detail(String id) {
        // 1. 按主键加载
        OrgPO po = orgMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Org not found");
        }
        // 3. 转为视图
        return toView(po);
    }

    @Override
    public PageResult<OrgView> page(OrgPageQuery query) {
        // 1. 数据库分页查询
        Page<OrgPO> page = orgMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<OrgPO>lambdaQuery()
                        .like(StringUtils.hasText(query.name()), OrgPO::getName, query.name())
                        .eq(StringUtils.hasText(query.category()), OrgPO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.status()), OrgPO::getStatus, query.status())
                        .eq(StringUtils.hasText(query.parentId()), OrgPO::getParentId, query.parentId())
                        .orderByAsc(OrgPO::getSort)
                        .orderByDesc(OrgPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<OrgView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Tree<String>> tree() {
        // 1. 拉取全部组织（树构建需完整父子关系）
        List<OrgPO> all = orgMapper.selectList(Wrappers.<OrgPO>lambdaQuery()
                .orderByAsc(OrgPO::getSort));
        if (all.isEmpty()) {
            return List.of();
        }
        // 2. 收集 ID，孤立父引用视为根
        Set<String> ids = all.stream().map(OrgPO::getId).collect(Collectors.toSet());
        TreeNodeConfig config = new TreeNodeConfig();
        config.setIdKey("id");
        config.setParentIdKey("parent_id");
        config.setNameKey("name");
        config.setWeightKey("weight");
        config.setChildrenKey("children");
        // 3. Hutool 建树并填充扩展字段
        return TreeUtil.build(all, null, config, (dept, tree) -> {
            String parentId = dept.getParentId();
            if (!StringUtils.hasText(parentId) || !ids.contains(parentId)) {
                parentId = null;
            }
            BeanUtil.beanToMap(dept, false, true).forEach((key, value) -> {
                if (!"children".equals(key)) {
                    tree.putExtra(StrUtil.toUnderlineCase(key), value);
                }
            });
            tree.setId(dept.getId());
            tree.setParentId(parentId);
            tree.setName(dept.getName());
            tree.setWeight(dept.getSort() == null ? 0 : dept.getSort());
        });
    }

    private OrgView toView(OrgPO po) {
        return new OrgView(
                po.getId(),
                po.getParentId(),
                po.getMasterId(),
                po.getDeputyMasterId(),
                po.getName(),
                po.getCategory(),
                po.getSort(),
                po.getIsVirtual(),
                po.getStatus(),
                po.getExtra(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }
}

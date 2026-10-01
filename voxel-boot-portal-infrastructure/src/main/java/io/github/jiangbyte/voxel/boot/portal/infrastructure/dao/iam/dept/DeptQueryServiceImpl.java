package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.dept;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port.DeptPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port.DeptQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port.DeptView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.Dept;
import lombok.RequiredArgsConstructor;

/**
 * 部门只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class DeptQueryServiceImpl implements DeptQueryService {

    private static final int IN_BATCH_SIZE = 500;

    private final DeptMapper deptMapper;


    @Override
    public DeptView detail(String id) {
        // 1. 按主键加载
        DeptPO po = deptMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Dept not found");
        }
        // 3. 转为视图
        return toView(po);
    }

    @Override
    public List<DeptView> listByIds(Collection<String> ids) {
        // 1. 去重过滤空 ID
        List<String> uniqueIds = normalizeIds(ids);
        if (uniqueIds.isEmpty()) {
            return List.of();
        }
        // 2. IN 分批查询，单批 ≤ 500，禁止循环单查
        List<DeptView> result = new ArrayList<>(uniqueIds.size());
        for (int i = 0; i < uniqueIds.size(); i += IN_BATCH_SIZE) {
            List<String> batch = uniqueIds.subList(i, Math.min(i + IN_BATCH_SIZE, uniqueIds.size()));
            for (DeptPO po : deptMapper.selectByIds(batch)) {
                result.add(toView(po));
            }
        }
        return result;
    }

    @Override
    public PageResult<DeptView> page(DeptPageQuery query) {
        // 1. 数据库分页查询
        Page<DeptPO> page = deptMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<DeptPO>lambdaQuery()
                        .like(StringUtils.hasText(query.name()), DeptPO::getName, query.name())
                        .eq(StringUtils.hasText(query.category()), DeptPO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.status()), DeptPO::getStatus, query.status())
                        .eq(StringUtils.hasText(query.parentId()), DeptPO::getParentId, query.parentId())
                        .orderByAsc(DeptPO::getSort)
                        .orderByDesc(DeptPO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<DeptView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Tree<String>> tree() {
        // 1. 拉取全部部门（树构建需完整父子关系）
        List<DeptPO> all = deptMapper.selectList(Wrappers.<DeptPO>lambdaQuery()
                .orderByAsc(DeptPO::getSort));
        if (all.isEmpty()) {
            return List.of();
        }
        // 2. 收集 ID，孤立父引用视为根
        Set<String> ids = all.stream().map(DeptPO::getId).collect(Collectors.toSet());
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

    private DeptView toView(DeptPO po) {
        return new DeptView(
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

    private static List<String> normalizeIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return ids.stream().filter(StringUtils::hasText).distinct().toList();
    }
}

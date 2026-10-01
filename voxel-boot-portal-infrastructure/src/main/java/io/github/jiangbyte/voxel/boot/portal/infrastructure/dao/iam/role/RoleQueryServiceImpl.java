package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port.RolePageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port.RoleQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port.RoleView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import lombok.RequiredArgsConstructor;

/**
 * 角色只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class RoleQueryServiceImpl implements RoleQueryService {

    private static final int IN_BATCH_SIZE = 500;

    private final RoleMapper roleMapper;


    @Override
    public RoleView detail(String id) {
        // 1. 按主键加载
        RolePO po = roleMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Role not found");
        }
        // 3. 转为视图
        return toView(po);
    }

    @Override
    public List<RoleView> listByIds(Collection<String> ids) {
        // 1. 去重过滤空 ID
        List<String> uniqueIds = normalizeIds(ids);
        if (uniqueIds.isEmpty()) {
            return List.of();
        }
        // 2. IN 分批查询，单批 ≤ 500，禁止循环单查
        List<RoleView> result = new ArrayList<>(uniqueIds.size());
        for (int i = 0; i < uniqueIds.size(); i += IN_BATCH_SIZE) {
            List<String> batch = uniqueIds.subList(i, Math.min(i + IN_BATCH_SIZE, uniqueIds.size()));
            for (RolePO po : roleMapper.selectByIds(batch)) {
                result.add(toView(po));
            }
        }
        return result;
    }

    @Override
    public PageResult<RoleView> page(RolePageQuery query) {
        // 1. 数据库分页查询
        Page<RolePO> page = roleMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<RolePO>lambdaQuery()
                        .like(StringUtils.hasText(query.code()), RolePO::getCode, query.code())
                        .like(StringUtils.hasText(query.name()), RolePO::getName, query.name())
                        .eq(StringUtils.hasText(query.category()), RolePO::getCategory, query.category())
                        .eq(StringUtils.hasText(query.scopeType()), RolePO::getScopeType, query.scopeType())
                        .eq(StringUtils.hasText(query.status()), RolePO::getStatus, query.status())
                        .orderByAsc(RolePO::getSort)
                        .orderByDesc(RolePO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<RoleView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private RoleView toView(RolePO po) {
        return new RoleView(
                po.getId(),
                po.getCode(),
                po.getName(),
                po.getCategory(),
                po.getScopeType(),
                po.getOwnerDeptId(),
                po.getSort(),
                po.getStatus(),
                po.getIsBuiltin(),
                po.getDescription(),
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

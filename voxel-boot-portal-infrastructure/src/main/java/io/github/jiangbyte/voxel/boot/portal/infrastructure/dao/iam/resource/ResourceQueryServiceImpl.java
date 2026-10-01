package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.AccountAuthorization;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourcePageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceTreeQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import lombok.RequiredArgsConstructor;

/**
 * 资源只读查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ResourceQueryServiceImpl implements ResourceQueryService {

    private static final List<String> TREE_TYPES = List.of("CATALOG", "MENU", "PAGE", "API_GROUP", "API");
    private static final String MODULE_CLIENT_ADMIN = "ADMIN";

    private final ResourceMapper resourceMapper;
    private final ResourceModuleMapper resourceModuleMapper;
    private final IamRelationService iamRelationService;
    private final SessionPort sessionPort;


    @Override
    public ResourceView detail(String id) {
        // 1. 按主键加载
        ResourcePO po = resourceMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "Resource not found");
        }
        // 3. 转为视图
        return toView(po);
    }

    @Override
    public PageResult<ResourceView> page(ResourcePageQuery query) {
        // 1. 数据库分页查询
        Page<ResourcePO> page = resourceMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<ResourcePO>lambdaQuery()
                        .like(StringUtils.hasText(query.code()), ResourcePO::getCode, query.code())
                        .like(StringUtils.hasText(query.name()), ResourcePO::getName, query.name())
                        .eq(StringUtils.hasText(query.resourceType()), ResourcePO::getResourceType, query.resourceType())
                        .eq(StringUtils.hasText(query.moduleId()), ResourcePO::getModuleId, query.moduleId())
                        .eq(StringUtils.hasText(query.status()), ResourcePO::getStatus, query.status())
                        .orderByAsc(ResourcePO::getSort)
                        .orderByDesc(ResourcePO::getCreatedAt));
        // 2. 映射为应用层 PageResult
        List<ResourceView> records = page.getRecords().stream().map(this::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Tree<String>> tree(ResourceTreeQuery query) {
        // 1. 拉取树节点类型资源（排除 BUTTON/ACTION）
        List<ResourcePO> all = resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                .in(ResourcePO::getResourceType, TREE_TYPES)
                .eq(StringUtils.hasText(query.moduleId()), ResourcePO::getModuleId, query.moduleId())
                .orderByAsc(ResourcePO::getSort));
        if (all.isEmpty()) {
            return List.of();
        }
        // 2. 收集 ID，孤立父引用视为根
        Set<String> ids = all.stream().map(ResourcePO::getId).collect(Collectors.toSet());
        TreeNodeConfig config = new TreeNodeConfig();
        config.setIdKey("id");
        config.setParentIdKey("parent_id");
        config.setNameKey("name");
        config.setWeightKey("weight");
        config.setChildrenKey("children");
        // 3. Hutool 建树并填充扩展字段
        return TreeUtil.build(all, null, config, (item, tree) -> {
            String parentId = item.getParentId();
            if (!StringUtils.hasText(parentId) || !ids.contains(parentId)) {
                parentId = null;
            }
            BeanUtil.beanToMap(item, false, true).forEach((key, value) -> {
                if (!"children".equals(key)) {
                    tree.putExtra(StrUtil.toUnderlineCase(key), value);
                }
            });
            tree.setId(item.getId());
            tree.setParentId(parentId);
            tree.setName(item.getName());
            tree.setWeight(item.getSort() == null ? 0 : item.getSort());
        });
    }

    @Override
    public List<ResourceView> current() {
        // 1. 必须已登录
        String accountId = sessionPort.currentAdminAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        // 2. 加载授权；超管或 *:*:* 返回管理端全部启用树资源
        AccountAuthorization auth = iamRelationService.getAccountAuthorization(accountId);
        boolean superAdmin = auth.getRoleCodes() != null
                && auth.getRoleCodes().contains(IamRelationTypes.SUPER_ADMIN);
        boolean allPerms = auth.getPermissionKeys() != null
                && auth.getPermissionKeys().contains("*:*:*");
        List<ResourcePO> resources;
        if (superAdmin || allPerms) {
            resources = listEnabledAdminTreeResources();
        } else if (auth.getResourceIds() == null || auth.getResourceIds().isEmpty()) {
            resources = List.of();
        } else {
            // 3. 普通账号按授权 id 补齐祖先
            resources = listResourcesByIdsWithParents(auth.getResourceIds());
        }
        // 4. 确保工作台首页可见
        resources = ensureWorkbenchHome(resources);
        return resources.stream().map(this::toView).toList();
    }

    private List<ResourcePO> listEnabledAdminTreeResources() {
        Set<String> moduleIds = moduleIdsByClient(MODULE_CLIENT_ADMIN);
        if (moduleIds.isEmpty()) {
            return List.of();
        }
        return resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                .in(ResourcePO::getModuleId, moduleIds)
                .eq(ResourcePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .in(ResourcePO::getResourceType, TREE_TYPES)
                .orderByAsc(ResourcePO::getSort)
                .orderByAsc(ResourcePO::getId));
    }

    private List<ResourcePO> listResourcesByIdsWithParents(List<String> resourceIds) {
        List<ResourcePO> all = listEnabledAdminTreeResources();
        if (all.isEmpty()) {
            return List.of();
        }
        Map<String, ResourcePO> byId = all.stream()
                .collect(Collectors.toMap(ResourcePO::getId, item -> item, (a, b) -> a, LinkedHashMap::new));
        Set<String> selected = new LinkedHashSet<>();
        for (String resourceId : resourceIds) {
            ResourcePO current = byId.get(resourceId);
            while (current != null && selected.add(current.getId())) {
                current = StringUtils.hasText(current.getParentId()) ? byId.get(current.getParentId()) : null;
            }
        }
        return all.stream().filter(item -> selected.contains(item.getId())).toList();
    }

    private List<ResourcePO> ensureWorkbenchHome(List<ResourcePO> resources) {
        boolean hasHome = resources.stream().anyMatch(item ->
                "sys-workspace".equals(item.getCode()) || "/sys/workspace".equals(item.getPath()));
        if (hasHome) {
            return resources;
        }
        ResourcePO home = resourceMapper.selectOne(Wrappers.<ResourcePO>lambdaQuery()
                .eq(ResourcePO::getCode, "sys-workspace")
                .eq(ResourcePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .last("LIMIT 1"));
        if (home == null) {
            return resources;
        }
        Set<String> moduleIds = moduleIdsByClient(MODULE_CLIENT_ADMIN);
        if (!moduleIds.contains(home.getModuleId())) {
            return resources;
        }
        List<ResourcePO> merged = new ArrayList<>(resources.size() + 1);
        merged.add(home);
        merged.addAll(resources);
        merged.sort(Comparator
                .comparing(ResourcePO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(ResourcePO::getId, Comparator.nullsLast(String::compareTo)));
        return merged;
    }

    private Set<String> moduleIdsByClient(String moduleClient) {
        return new HashSet<>(resourceModuleMapper.selectList(Wrappers.<ResourceModulePO>lambdaQuery()
                        .eq(ResourceModulePO::getClient, moduleClient))
                .stream()
                .map(ResourceModulePO::getId)
                .toList());
    }

    private ResourceView toView(ResourcePO po) {
        return new ResourceView(
                po.getId(),
                po.getParentId(),
                po.getCode(),
                po.getName(),
                po.getResourceType(),
                po.getModuleId(),
                po.getPath(),
                po.getComponent(),
                po.getRedirect(),
                po.getIcon(),
                po.getColor(),
                po.getHref(),
                po.getSort(),
                po.getIsVisible(),
                po.getIsCache(),
                po.getIsAffix(),
                po.getStatus(),
                po.getDescription(),
                po.getLayout(),
                po.getExtra(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.relation;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantCatalogPort;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantMenuOption;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantModuleOption;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourcePermissionOption;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client.ClientModuleMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client.ClientModulePO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client.ClientResourceMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client.ClientResourcePO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceModuleMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceModulePO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourcePO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/**
 * 资源授权目录：组装模块→菜单→按钮权限选项树。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ResourceGrantCatalogService implements ResourceGrantCatalogPort {

    private static final Set<String> GRANT_MENU_TYPES = Set.of("CATALOG", "MENU", "PAGE", "API_GROUP", "API");

    private final ResourceModuleMapper resourceModuleMapper;
    private final ResourceMapper resourceMapper;
    private final ClientModuleMapper clientModuleMapper;
    private final ClientResourceMapper clientResourceMapper;
    private final IamRelationMapper relationMapper;


    @Override
    public List<ResourceGrantModuleOption> listAdminGrantModules(String moduleClient) {
        // 1. 加载启用模块
        var moduleQuery = Wrappers.<ResourceModulePO>lambdaQuery()
                .eq(ResourceModulePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .orderByAsc(ResourceModulePO::getSort);
        if (StringUtils.hasText(moduleClient)) {
            moduleQuery.eq(ResourceModulePO::getClient, moduleClient);
        }
        List<ResourceModulePO> modules = resourceModuleMapper.selectList(moduleQuery);
        Set<String> moduleIds = modules.stream().map(ResourceModulePO::getId).collect(Collectors.toSet());
        if (moduleIds.isEmpty() && StringUtils.hasText(moduleClient)) {
            return List.of();
        }
        // 2. 加载模块下启用资源
        var resourceQuery = Wrappers.<ResourcePO>lambdaQuery()
                .eq(ResourcePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .orderByAsc(ResourcePO::getSort);
        if (!moduleIds.isEmpty()) {
            resourceQuery.in(ResourcePO::getModuleId, moduleIds);
        }
        List<ResourcePO> resources = resourceMapper.selectList(resourceQuery);
        if (resources.isEmpty()) {
            return List.of();
        }
        // 3. 权限绑定
        List<IamRelationPO> permissions = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_RESOURCE)
                .eq(IamRelationPO::getRelationType, IamRelationTypes.RESOURCE_PERMISSION)
                .eq(StringUtils.hasText(moduleClient), IamRelationPO::getAccountType, moduleClient));
        return assemble(modules.stream().map(m -> new ModuleMeta(m.getId(), m.getName(), m.getSort())).toList(),
                resources.stream().map(r -> new ResMeta(r.getId(), r.getParentId(), r.getModuleId(),
                        r.getName(), r.getCode(), r.getResourceType())).toList(),
                permissions);
    }

    @Override
    public List<ResourceGrantModuleOption> listClientGrantModules(String accountType) {
        // 1. 加载启用客户端模块
        var moduleQuery = Wrappers.<ClientModulePO>lambdaQuery()
                .eq(ClientModulePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .orderByAsc(ClientModulePO::getSort);
        if (StringUtils.hasText(accountType)) {
            moduleQuery.eq(ClientModulePO::getAccountType, accountType);
        }
        List<ClientModulePO> modules = clientModuleMapper.selectList(moduleQuery);
        Set<String> moduleIds = modules.stream().map(ClientModulePO::getId).collect(Collectors.toSet());
        if (moduleIds.isEmpty() && StringUtils.hasText(accountType)) {
            return List.of();
        }
        // 2. 加载客户端资源
        var resourceQuery = Wrappers.<ClientResourcePO>lambdaQuery()
                .eq(ClientResourcePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .orderByAsc(ClientResourcePO::getSort);
        if (!moduleIds.isEmpty()) {
            resourceQuery.in(ClientResourcePO::getModuleId, moduleIds);
        }
        List<ClientResourcePO> resources = clientResourceMapper.selectList(resourceQuery);
        if (resources.isEmpty()) {
            return List.of();
        }
        // 3. 客户端权限绑定
        List<IamRelationPO> permissions = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_CLIENT_RESOURCE)
                .eq(IamRelationPO::getRelationType, IamRelationTypes.CLIENT_RESOURCE_PERMISSION)
                .eq(StringUtils.hasText(accountType), IamRelationPO::getAccountType, accountType));
        return assemble(modules.stream().map(m -> new ModuleMeta(m.getId(), m.getName(), m.getSort())).toList(),
                resources.stream().map(r -> new ResMeta(r.getId(), r.getParentId(), r.getModuleId(),
                        r.getName(), r.getCode(), r.getResourceType())).toList(),
                permissions);
    }

    private List<ResourceGrantModuleOption> assemble(List<ModuleMeta> modules, List<ResMeta> resources,
                                                     List<IamRelationPO> permissions) {
        // 1. 按资源汇总权限选项
        Map<String, List<ResourcePermissionOption>> permissionMap = new HashMap<>();
        for (IamRelationPO permission : permissions) {
            ResourcePermissionOption option = new ResourcePermissionOption();
            option.setId(permission.getId());
            option.setPermissionKey(permission.getTargetKey());
            option.setTitle(StringUtils.hasText(permission.getDescription())
                    ? permission.getDescription() : permission.getTargetKey());
            option.setDataScope(permission.getDataScope());
            permissionMap.computeIfAbsent(permission.getSubjectId(), key -> new ArrayList<>()).add(option);
        }
        // 2. 按钮/动作权限挂到父菜单
        Map<String, ResMeta> resourceMap = resources.stream()
                .collect(Collectors.toMap(ResMeta::id, item -> item, (a, b) -> a, LinkedHashMap::new));
        Map<String, List<ResourcePermissionOption>> childPermissionMap = new HashMap<>();
        for (ResMeta resource : resources) {
            if (!"BUTTON".equals(resource.resourceType()) && !"ACTION".equals(resource.resourceType())) {
                continue;
            }
            if (!StringUtils.hasText(resource.parentId())) {
                continue;
            }
            List<ResourcePermissionOption> options = permissionMap.get(resource.id());
            if (options == null || options.isEmpty()) {
                ResourcePermissionOption fallback = new ResourcePermissionOption();
                fallback.setId(resource.id());
                fallback.setPermissionKey(resource.code());
                fallback.setTitle(resource.name());
                options = List.of(fallback);
            }
            childPermissionMap.computeIfAbsent(resource.parentId(), key -> new ArrayList<>()).addAll(options);
        }
        // 3. 组装模块 → 菜单
        Map<String, ResourceGrantModuleOption> moduleMap = new LinkedHashMap<>();
        Map<String, Integer> moduleSortMap = new HashMap<>();
        for (ModuleMeta module : modules) {
            ResourceGrantModuleOption option = new ResourceGrantModuleOption();
            option.setId(module.id());
            option.setTitle(module.name());
            moduleMap.put(module.id(), option);
            moduleSortMap.put(module.id(), module.sort() == null ? 99 : module.sort());
        }
        for (ResMeta resource : resources) {
            if (!GRANT_MENU_TYPES.contains(resource.resourceType()) || !StringUtils.hasText(resource.moduleId())) {
                continue;
            }
            String moduleId = resource.moduleId();
            ResourceGrantModuleOption module = moduleMap.computeIfAbsent(moduleId, key -> {
                ResourceGrantModuleOption created = new ResourceGrantModuleOption();
                created.setId(key);
                created.setTitle(key);
                moduleSortMap.putIfAbsent(key, 99);
                return created;
            });
            ResMeta parent = resourceMap.get(resource.parentId());
            ResourceGrantMenuOption menu = new ResourceGrantMenuOption();
            menu.setId(resource.id());
            menu.setModuleId(moduleId);
            menu.setParentId(resource.parentId());
            menu.setParentIdName(parent != null ? parent.name() : resource.name());
            menu.setTitle(resource.name());
            List<ResourcePermissionOption> buttons = new ArrayList<>();
            buttons.addAll(permissionMap.getOrDefault(resource.id(), List.of()));
            buttons.addAll(childPermissionMap.getOrDefault(resource.id(), List.of()));
            menu.setButton(buttons);
            module.getMenu().add(menu);
        }
        // 4. 过滤空模块并按排序返回
        return moduleMap.values().stream()
                .filter(item -> item.getMenu() != null && !item.getMenu().isEmpty())
                .sorted(Comparator
                        .comparing((ResourceGrantModuleOption item) -> moduleSortMap.getOrDefault(item.getId(), 99))
                        .thenComparing(ResourceGrantModuleOption::getId))
                .toList();
    }

    private record ModuleMeta(String id, String name, Integer sort) {}
    private record ResMeta(String id, String parentId, String moduleId, String name, String code, String resourceType) {}
}

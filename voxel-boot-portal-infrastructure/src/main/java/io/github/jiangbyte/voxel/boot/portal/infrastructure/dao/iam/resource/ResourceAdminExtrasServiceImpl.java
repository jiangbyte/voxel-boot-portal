package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.PermissionRegistryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceAdminExtrasService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceButtonView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceModuleView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.relation.IamRelationMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.relation.IamRelationPO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support.BatchPartition;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support.MapBodies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import lombok.RequiredArgsConstructor;

/**
 * 资源模块/按钮/权限绑定与门户资源实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ResourceAdminExtrasServiceImpl implements ResourceAdminExtrasService {

    private static final List<String> TREE_TYPES = List.of("CATALOG", "MENU", "PAGE", "API_GROUP", "API");

    private final ResourceModuleMapper moduleMapper;
    private final ResourceMapper resourceMapper;
    private final IamRelationService relationService;
    private final IamRelationMapper relationMapper;
    private final PermissionRegistryService permissionRegistryService;


    @Override
    @Transactional
    public void createModule(Map<String, Object> body) {
        // 1. 组装 PO
        ResourceModulePO po = new ResourceModulePO();
        fillModule(po, body);
        // 2. 插入
        moduleMapper.insert(po);
    }

    @Override
    @Transactional
    public void updateModule(Map<String, Object> body) {
        // 1. 加载
        String id = MapBodies.str(body, "id");
        ResourceModulePO po = moduleMapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Resource module not found");
        }
        // 2. 覆盖字段并更新
        fillModule(po, body);
        moduleMapper.updateById(po);
    }

    @Override
    @Transactional
    public void deleteModules(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        moduleMapper.deleteByIds(ids);
    }

    @Override
    public ResourceModuleView moduleDetail(String id) {
        ResourceModulePO po = moduleMapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Resource module not found");
        }
        return toModuleView(po);
    }

    @Override
    public PageResult<ResourceModuleView> pageModules(long current, long size, String code, String name, String status) {
        Page<ResourceModulePO> page = moduleMapper.selectPage(new Page<>(current, size),
                Wrappers.<ResourceModulePO>lambdaQuery()
                        .like(StringUtils.hasText(code), ResourceModulePO::getCode, code)
                        .like(StringUtils.hasText(name), ResourceModulePO::getName, name)
                        .eq(StringUtils.hasText(status), ResourceModulePO::getStatus, status)
                        .orderByAsc(ResourceModulePO::getSort));
        return new PageResult<>(page.getRecords().stream().map(this::toModuleView).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<ResourceModuleView> moduleSelector() {
        return moduleMapper.selectList(Wrappers.<ResourceModulePO>lambdaQuery()
                        .eq(ResourceModulePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                        .orderByAsc(ResourceModulePO::getSort))
                .stream().map(this::toModuleView).toList();
    }

    @Override
    @Transactional
    public void createButton(Map<String, Object> body) {
        // 1. 校验父资源
        String parentId = MapBodies.str(body, "parentId");
        ResourcePO parent = resourceMapper.selectById(parentId);
        if (parent == null) {
            throw new DomainException(404, "Resource not found");
        }
        if ("BUTTON".equals(parent.getResourceType()) || "ACTION".equals(parent.getResourceType())) {
            throw new DomainException("Button resource cannot be parent resource");
        }
        String permissionKey = MapBodies.str(body, "permissionKey");
        permissionRegistryService.ensureRegistered(permissionKey);
        // 2. 创建按钮资源
        ResourcePO button = new ResourcePO();
        button.setParentId(parent.getId());
        button.setCode(MapBodies.str(body, "code"));
        button.setName(MapBodies.str(body, "name"));
        button.setResourceType("BUTTON");
        button.setModuleId(parent.getModuleId());
        button.setSort(MapBodies.integer(body, "sort") == null ? 99 : MapBodies.integer(body, "sort"));
        button.setIsVisible(Flags.of(false));
        button.setIsCache(Flags.of(false));
        button.setIsAffix(Flags.of(false));
        String status = MapBodies.str(body, "status");
        button.setStatus(StringUtils.hasText(status) ? status : IamRelationTypes.STATUS_ENABLED);
        button.setDescription(MapBodies.str(body, "description"));
        resourceMapper.insert(button);
        // 3. 绑定权限
        replaceButtonPermission(button.getId(), permissionKey,
                MapBodies.str(body, "dataScope"), MapBodies.stringList(body, "customScopeDeptIds"),
                MapBodies.integer(body, "sort"), MapBodies.str(body, "description"));
    }

    @Override
    @Transactional
    public void updateButton(Map<String, Object> body) {
        ResourcePO button = resourceMapper.selectById(MapBodies.str(body, "id"));
        if (button == null) {
            throw new DomainException(404, "Resource not found");
        }
        if (!"BUTTON".equals(button.getResourceType())) {
            throw new DomainException("Resource is not a button");
        }
        ResourcePO parent = resourceMapper.selectById(MapBodies.str(body, "parentId"));
        if (parent == null) {
            throw new DomainException(404, "Resource not found");
        }
        if ("BUTTON".equals(parent.getResourceType()) || "ACTION".equals(parent.getResourceType())) {
            throw new DomainException("Button resource cannot be parent resource");
        }
        String permissionKey = MapBodies.str(body, "permissionKey");
        permissionRegistryService.ensureRegistered(permissionKey);
        button.setParentId(parent.getId());
        button.setCode(MapBodies.str(body, "code"));
        button.setName(MapBodies.str(body, "name"));
        button.setModuleId(parent.getModuleId());
        button.setSort(MapBodies.integer(body, "sort") == null ? 99 : MapBodies.integer(body, "sort"));
        String status = MapBodies.str(body, "status");
        button.setStatus(StringUtils.hasText(status) ? status : IamRelationTypes.STATUS_ENABLED);
        button.setDescription(MapBodies.str(body, "description"));
        resourceMapper.updateById(button);
        replaceButtonPermission(button.getId(), permissionKey,
                MapBodies.str(body, "dataScope"), MapBodies.stringList(body, "customScopeDeptIds"),
                MapBodies.integer(body, "sort"), MapBodies.str(body, "description"));
    }

    @Override
    @Transactional
    public void deleteButtons(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<ResourcePO> buttons = new ArrayList<>();
        for (List<String> batch : BatchPartition.partition(ids)) {
            buttons.addAll(resourceMapper.selectByIds(batch));
        }
        if (buttons.size() != new LinkedHashSet<>(ids).size()) {
            throw new DomainException(404, "Resource not found");
        }
        for (ResourcePO button : buttons) {
            if (!"BUTTON".equals(button.getResourceType())) {
                throw new DomainException("Resource is not a button");
            }
        }
        relationService.deleteSubjectRelations(
                IamRelationTypes.SUBJECT_RESOURCE, ids, IamRelationTypes.RESOURCE_PERMISSION);
        for (List<String> batch : BatchPartition.partition(ids)) {
            resourceMapper.deleteByIds(batch);
        }
    }

    @Override
    public PageResult<ResourceButtonView> pageButtons(long current, long size, String parentId,
                                                      String code, String name, String status) {
        Page<ResourcePO> page = resourceMapper.selectPage(new Page<>(current, size),
                Wrappers.<ResourcePO>lambdaQuery()
                        .eq(ResourcePO::getParentId, parentId)
                        .eq(ResourcePO::getResourceType, "BUTTON")
                        .like(StringUtils.hasText(code), ResourcePO::getCode, code)
                        .like(StringUtils.hasText(name), ResourcePO::getName, name)
                        .eq(StringUtils.hasText(status), ResourcePO::getStatus, status)
                        .orderByAsc(ResourcePO::getSort)
                        .orderByDesc(ResourcePO::getId));
        Map<String, IamRelationPO> permByResource = loadButtonPermissions(
                page.getRecords().stream().map(ResourcePO::getId).toList());
        List<ResourceButtonView> records = page.getRecords().stream().map(po -> {
            IamRelationPO perm = permByResource.get(po.getId());
            return new ResourceButtonView(
                    po.getId(), po.getParentId(), po.getCode(), po.getName(), po.getStatus(), po.getSort(),
                    po.getDescription(),
                    perm == null ? null : perm.getTargetKey(),
                    perm == null ? null : perm.getDataScope(),
                    perm == null ? List.of() : (perm.getCustomScopeDeptIds() == null ? List.of() : perm.getCustomScopeDeptIds()),
                    po.getCreatedAt(), po.getUpdatedAt());
        }).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional
    public void bindPermission(Map<String, Object> body) {
        String resourceId = MapBodies.str(body, "resourceId");
        ResourcePO resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new DomainException(404, "Resource not found");
        }
        String permissionKey = MapBodies.str(body, "permissionKey");
        permissionRegistryService.ensureRegistered(permissionKey);
        relationService.bindResourcePermission(
                resourceId,
                permissionKey,
                MapBodies.str(body, "accountType"),
                MapBodies.str(body, "dataScope"),
                MapBodies.stringList(body, "customScopeDeptIds"),
                MapBodies.integer(body, "sort"),
                MapBodies.str(body, "description"));
    }

    @Override
    public List<ResourceView> listPortalCurrent() {
        // 门户公开资源：启用的 PORTAL 模块下树节点类型资源
        List<ResourceModulePO> modules = moduleMapper.selectList(Wrappers.<ResourceModulePO>lambdaQuery()
                .eq(ResourceModulePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .eq(ResourceModulePO::getClient, "PORTAL"));
        if (modules.isEmpty()) {
            return List.of();
        }
        Set<String> moduleIds = new LinkedHashSet<>();
        modules.forEach(m -> moduleIds.add(m.getId()));
        List<ResourcePO> list = resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                .eq(ResourcePO::getStatus, IamRelationTypes.STATUS_ENABLED)
                .in(ResourcePO::getModuleId, moduleIds)
                .in(ResourcePO::getResourceType, TREE_TYPES)
                .orderByAsc(ResourcePO::getSort));
        return list.stream().map(this::toResourceView).toList();
    }

    private void replaceButtonPermission(String buttonId, String permissionKey, String dataScope,
                                         List<String> customScopeDeptIds, Integer sort, String description) {
        relationService.deleteSubjectRelations(
                IamRelationTypes.SUBJECT_RESOURCE, buttonId, IamRelationTypes.RESOURCE_PERMISSION);
        relationService.bindResourcePermission(
                buttonId, permissionKey, "ADMIN", dataScope, customScopeDeptIds, sort, description);
    }

    private Map<String, IamRelationPO> loadButtonPermissions(List<String> buttonIds) {
        Map<String, IamRelationPO> map = new HashMap<>();
        if (buttonIds == null || buttonIds.isEmpty()) {
            return map;
        }
        List<IamRelationPO> rows = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_RESOURCE)
                .eq(IamRelationPO::getRelationType, IamRelationTypes.RESOURCE_PERMISSION)
                .in(IamRelationPO::getSubjectId, buttonIds));
        for (IamRelationPO row : rows) {
            map.putIfAbsent(row.getSubjectId(), row);
        }
        return map;
    }

    private void fillModule(ResourceModulePO po, Map<String, Object> body) {
        po.setName(MapBodies.str(body, "name"));
        po.setCode(MapBodies.str(body, "code"));
        po.setClient(MapBodies.str(body, "client"));
        po.setIcon(MapBodies.str(body, "icon"));
        po.setColor(MapBodies.str(body, "color"));
        po.setSort(MapBodies.integer(body, "sort"));
        String status = MapBodies.str(body, "status");
        po.setStatus(StringUtils.hasText(status) ? status : IamRelationTypes.STATUS_ENABLED);
        po.setDescription(MapBodies.str(body, "description"));
        po.setExtra(MapBodies.map(body, "extra"));
    }

    private ResourceModuleView toModuleView(ResourceModulePO po) {
        return new ResourceModuleView(po.getId(), po.getName(), po.getCode(), po.getClient(), po.getIcon(),
                po.getColor(), po.getSort(), po.getStatus(), po.getDescription(), po.getExtra(),
                po.getCreatedAt(), po.getUpdatedAt());
    }

    private ResourceView toResourceView(ResourcePO po) {
        return new ResourceView(po.getId(), po.getParentId(), po.getCode(), po.getName(), po.getResourceType(),
                po.getModuleId(), po.getPath(), po.getComponent(), po.getRedirect(), po.getIcon(), po.getColor(),
                po.getHref(), po.getSort(), po.getIsVisible(), po.getIsCache(), po.getIsAffix(), po.getStatus(),
                po.getDescription(), po.getLayout(), po.getExtra(), po.getCreatedAt(), po.getUpdatedAt());
    }
}

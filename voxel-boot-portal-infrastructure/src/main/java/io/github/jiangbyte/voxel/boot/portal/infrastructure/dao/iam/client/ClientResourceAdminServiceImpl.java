package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port.ClientResourceAdminService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.client.adapter.port.ClientResourceView;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.PermissionRegistryService;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support.MapBodies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/**
 * 客户端资源管理实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class ClientResourceAdminServiceImpl implements ClientResourceAdminService {

    private static final List<String> TREE_TYPES = List.of("CATALOG", "MENU", "PAGE", "API_GROUP", "API");

    private final ClientResourceMapper mapper;
    private final IamRelationService relationService;
    private final PermissionRegistryService permissionRegistryService;


    @Override
    @Transactional
    public void create(Map<String, Object> body) {
        ClientResourcePO po = new ClientResourcePO();
        fill(po, body);
        mapper.insert(po);
    }

    @Override
    @Transactional
    public void update(Map<String, Object> body) {
        ClientResourcePO po = mapper.selectById(MapBodies.str(body, "id"));
        if (po == null) {
            throw new DomainException(404, "Client resource not found");
        }
        fill(po, body);
        mapper.updateById(po);
    }

    @Override
    @Transactional
    public void delete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        relationService.deleteBySubjectIds(IamRelationTypes.SUBJECT_CLIENT_RESOURCE, ids);
        relationService.deleteByTargetIds(IamRelationTypes.TARGET_CLIENT_RESOURCE, ids);
        mapper.deleteByIds(ids);
    }

    @Override
    public ClientResourceView detail(String id) {
        ClientResourcePO po = mapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Client resource not found");
        }
        return toView(po);
    }

    @Override
    public PageResult<ClientResourceView> page(long current, long size, String code, String name,
                                               String moduleId, String resourceType, String status) {
        Page<ClientResourcePO> page = mapper.selectPage(new Page<>(current, size),
                Wrappers.<ClientResourcePO>lambdaQuery()
                        .like(StringUtils.hasText(code), ClientResourcePO::getCode, code)
                        .like(StringUtils.hasText(name), ClientResourcePO::getName, name)
                        .eq(StringUtils.hasText(moduleId), ClientResourcePO::getModuleId, moduleId)
                        .eq(StringUtils.hasText(resourceType), ClientResourcePO::getResourceType, resourceType)
                        .eq(StringUtils.hasText(status), ClientResourcePO::getStatus, status)
                        .orderByAsc(ClientResourcePO::getSort));
        return new PageResult<>(page.getRecords().stream().map(this::toView).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Tree<String>> tree(String moduleId) {
        List<ClientResourcePO> all = mapper.selectList(Wrappers.<ClientResourcePO>lambdaQuery()
                .in(ClientResourcePO::getResourceType, TREE_TYPES)
                .eq(StringUtils.hasText(moduleId), ClientResourcePO::getModuleId, moduleId)
                .orderByAsc(ClientResourcePO::getSort));
        if (all.isEmpty()) {
            return List.of();
        }
        Set<String> ids = all.stream().map(ClientResourcePO::getId).collect(Collectors.toSet());
        TreeNodeConfig config = new TreeNodeConfig();
        config.setIdKey("id");
        config.setParentIdKey("parent_id");
        config.setNameKey("name");
        config.setWeightKey("weight");
        config.setChildrenKey("children");
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
    @Transactional
    public void bindPermission(Map<String, Object> body) {
        String resourceId = MapBodies.str(body, "resourceId");
        if (mapper.selectById(resourceId) == null) {
            throw new DomainException(404, "Client resource not found");
        }
        String permissionKey = MapBodies.str(body, "permissionKey");
        permissionRegistryService.ensureRegistered(permissionKey);
        relationService.bindClientResourcePermission(
                resourceId,
                permissionKey,
                MapBodies.str(body, "accountType"),
                MapBodies.str(body, "dataScope"),
                MapBodies.stringList(body, "customScopeDeptIds"),
                MapBodies.integer(body, "sort"),
                MapBodies.str(body, "description"));
    }

    private void fill(ClientResourcePO po, Map<String, Object> body) {
        po.setParentId(MapBodies.str(body, "parentId"));
        po.setCode(MapBodies.str(body, "code"));
        po.setName(MapBodies.str(body, "name"));
        po.setResourceType(MapBodies.str(body, "resourceType"));
        po.setModuleId(MapBodies.str(body, "moduleId"));
        po.setPath(MapBodies.str(body, "path"));
        po.setComponent(MapBodies.str(body, "component"));
        po.setRedirect(MapBodies.str(body, "redirect"));
        po.setIcon(MapBodies.str(body, "icon"));
        po.setColor(MapBodies.str(body, "color"));
        po.setHref(MapBodies.str(body, "href"));
        po.setSort(MapBodies.integer(body, "sort"));
        po.setIsVisible(Flags.of(MapBodies.integer(body, "isVisible")));
        po.setIsCache(Flags.of(MapBodies.integer(body, "isCache")));
        po.setIsAffix(Flags.of(MapBodies.integer(body, "isAffix")));
        String status = MapBodies.str(body, "status");
        po.setStatus(StringUtils.hasText(status) ? status : IamRelationTypes.STATUS_ENABLED);
        po.setDescription(MapBodies.str(body, "description"));
        po.setLayout(MapBodies.str(body, "layout"));
        po.setExtra(MapBodies.map(body, "extra"));
    }

    private ClientResourceView toView(ClientResourcePO po) {
        return new ClientResourceView(po.getId(), po.getParentId(), po.getCode(), po.getName(), po.getResourceType(),
                po.getModuleId(), po.getPath(), po.getComponent(), po.getRedirect(), po.getIcon(), po.getColor(),
                po.getHref(), po.getSort(), po.getIsVisible(), po.getIsCache(), po.getIsAffix(), po.getStatus(),
                po.getDescription(), po.getLayout(), po.getExtra(), po.getCreatedAt(), po.getUpdatedAt());
    }
}

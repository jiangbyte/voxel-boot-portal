package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.relation;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.AccountAuthorization;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.DeptGrantItem;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.PermissionGrant;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantItem;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.account.AccountPO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client.ClientResourceMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.client.ClientResourcePO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourcePO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role.RoleMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role.RolePO;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.support.BatchPartition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.Resource;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import lombok.RequiredArgsConstructor;

/**
 * IAM 关系服务实现：批量查询关系、组装授权视图，以及授予关系的删除后重建。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class IamRelationServiceImpl implements IamRelationService {

    private final IamRelationMapper relationMapper;
    private final RoleMapper roleMapper;
    private final ResourceMapper resourceMapper;
    private final ClientResourceMapper clientResourceMapper;
    private final AccountMapper accountMapper;
    private final SessionPort sessionPort;


    @Override
    public AccountAuthorization getAccountAuthorization(String accountId) {
        return getAccountsAuthorization(List.of(accountId)).getOrDefault(accountId, new AccountAuthorization());
    }

    @Override
    public Map<String, AccountAuthorization> getAccountsMembership(Collection<String> accountIds) {
        return loadAccountsAuthorization(accountIds, false);
    }

    @Override
    public Map<String, AccountAuthorization> getAccountsAuthorization(Collection<String> accountIds) {
        return loadAccountsAuthorization(accountIds, true);
    }

    private Map<String, AccountAuthorization> loadAccountsAuthorization(
            Collection<String> accountIds, boolean includeResourcePermissions) {
        // 1. 去重账号并预置空授权壳
        List<String> uniqueIds = accountIds == null ? List.of()
                : accountIds.stream().filter(StringUtils::hasText).distinct().toList();
        Map<String, AccountAuthorization> result = new LinkedHashMap<>();
        for (String accountId : uniqueIds) {
            result.put(accountId, new AccountAuthorization());
        }
        if (uniqueIds.isEmpty()) {
            return result;
        }

        // 2. 批量查账号类型，后续关系行按 accountType 过滤
        Map<String, String> accountTypeMap = loadAccountTypeMap(uniqueIds);

        // 3. 一次查出账号直接关系：组 / 部门 / 角色
        List<IamRelationPO> accountRels = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_ACCOUNT)
                .in(IamRelationPO::getSubjectId, uniqueIds)
                .in(IamRelationPO::getRelationType, List.of(
                        IamRelationTypes.ACCOUNT_GROUP,
                        IamRelationTypes.ACCOUNT_DEPT,
                        IamRelationTypes.ACCOUNT_ROLE)));

        Map<String, Set<String>> groupIdsByAccount = new HashMap<>();
        Map<String, Set<String>> roleIdsByAccount = new HashMap<>();
        Map<String, Set<String>> accountIdsByGroup = new HashMap<>();
        Map<String, Set<String>> accountIdsByRole = new HashMap<>();

        // 4. 汇总到 AccountAuthorization，并建组/角色 ↔ 账号反向索引
        for (IamRelationPO rel : accountRels) {
            String accountId = rel.getSubjectId();
            AccountAuthorization auth = result.get(accountId);
            if (auth == null || !matchesAccountType(accountTypeMap.get(accountId), rel.getAccountType())) {
                continue;
            }
            if (IamRelationTypes.ACCOUNT_GROUP.equals(rel.getRelationType())) {
                auth.getGroupIds().add(rel.getTargetId());
                groupIdsByAccount.computeIfAbsent(accountId, key -> new HashSet<>()).add(rel.getTargetId());
                accountIdsByGroup.computeIfAbsent(rel.getTargetId(), key -> new HashSet<>()).add(accountId);
            } else if (IamRelationTypes.ACCOUNT_DEPT.equals(rel.getRelationType())) {
                auth.getDeptIds().add(rel.getTargetId());
            } else if (IamRelationTypes.ACCOUNT_ROLE.equals(rel.getRelationType())) {
                roleIdsByAccount.computeIfAbsent(accountId, key -> new HashSet<>()).add(rel.getTargetId());
                accountIdsByRole.computeIfAbsent(rel.getTargetId(), key -> new HashSet<>()).add(accountId);
            }
        }

        // 5. 经用户组展开继承角色
        Set<String> allGroupIds = accountIdsByGroup.keySet();
        if (!allGroupIds.isEmpty()) {
            List<IamRelationPO> groupRoles = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                    .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_GROUP)
                    .in(IamRelationPO::getSubjectId, allGroupIds)
                    .eq(IamRelationPO::getRelationType, IamRelationTypes.GROUP_ROLE)
                    .eq(IamRelationPO::getTargetType, IamRelationTypes.TARGET_ROLE));
            for (IamRelationPO rel : groupRoles) {
                for (String accountId : accountIdsByGroup.getOrDefault(rel.getSubjectId(), Set.of())) {
                    if (!matchesAccountType(accountTypeMap.get(accountId), rel.getAccountType())) {
                        continue;
                    }
                    roleIdsByAccount.computeIfAbsent(accountId, key -> new HashSet<>()).add(rel.getTargetId());
                    accountIdsByRole.computeIfAbsent(rel.getTargetId(), key -> new HashSet<>()).add(accountId);
                }
            }
        }

        // 6. 批量加载角色编码并回填
        Set<String> allRoleIds = roleIdsByAccount.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
        Map<String, String> roleCodeMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            for (RolePO role : roleMapper.selectByIds(allRoleIds)) {
                roleCodeMap.put(role.getId(), role.getCode());
            }
        }
        for (Map.Entry<String, Set<String>> entry : roleIdsByAccount.entrySet()) {
            AccountAuthorization auth = result.get(entry.getKey());
            List<String> sortedRoleIds = entry.getValue().stream().sorted().toList();
            auth.setRoleIds(new ArrayList<>(sortedRoleIds));
            auth.setRoleCodes(sortedRoleIds.stream()
                    .map(roleCodeMap::get)
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList());
        }

        // 7. 组、部门去重排序；仅要成员关系时到此返回
        for (String accountId : uniqueIds) {
            AccountAuthorization auth = result.get(accountId);
            auth.setGroupIds(auth.getGroupIds().stream().distinct().sorted().toList());
            auth.setDeptIds(auth.getDeptIds().stream().distinct().sorted().toList());
        }
        if (!includeResourcePermissions) {
            return result;
        }

        // 8. 合并账号/组/角色上的后台与客户端资源授予，展开为权限键
        Map<String, List<IamRelationPO>> resourceGrantsByAccount = listResourceGrantsByAccount(
                uniqueIds, accountIdsByGroup, accountIdsByRole, accountTypeMap);
        Map<String, List<PermissionGrant>> permissionGrantsByAccount =
                listPermissionGrantsByAccount(resourceGrantsByAccount, accountTypeMap);
        Map<String, List<IamRelationPO>> clientResourceGrantsByAccount = listClientResourceGrantsByAccount(
                uniqueIds, accountIdsByGroup, accountIdsByRole, accountTypeMap);
        Map<String, Set<String>> clientPermissionKeysByAccount =
                listClientPermissionKeysByAccount(clientResourceGrantsByAccount, accountTypeMap);

        // 9. 回填资源 ID、权限授予明细与权限键；超管补通配 *:*:*
        for (String accountId : uniqueIds) {
            AccountAuthorization auth = result.get(accountId);
            List<IamRelationPO> grants = resourceGrantsByAccount.getOrDefault(accountId, List.of());
            auth.setResourceIds(grants.stream().map(IamRelationPO::getTargetId).distinct().sorted().toList());
            List<PermissionGrant> permissionGrants = permissionGrantsByAccount.getOrDefault(accountId, List.of());
            auth.setPermissionGrants(permissionGrants);
            List<String> keys = permissionGrants.stream()
                    .map(PermissionGrant::getPermissionKey)
                    .distinct()
                    .sorted()
                    .toList();
            auth.setPermissionKeys(new ArrayList<>(keys));
            auth.setButtonCodes(new ArrayList<>(keys));
            List<IamRelationPO> clientGrants = clientResourceGrantsByAccount.getOrDefault(accountId, List.of());
            auth.setClientResourceIds(clientGrants.stream().map(IamRelationPO::getTargetId).distinct().sorted().toList());
            auth.setClientPermissionKeys(clientPermissionKeysByAccount.getOrDefault(accountId, Set.of()).stream()
                    .sorted().toList());
            if (auth.getRoleCodes().contains(IamRelationTypes.SUPER_ADMIN)
                    && !auth.getPermissionKeys().contains("*:*:*")) {
                auth.getPermissionKeys().add("*:*:*");
                auth.getButtonCodes().add("*:*:*");
            }
        }
        return result;
    }

    private Map<String, List<IamRelationPO>> listResourceGrantsByAccount(
            List<String> accountIds,
            Map<String, Set<String>> accountIdsByGroup,
            Map<String, Set<String>> accountIdsByRole,
            Map<String, String> accountTypeMap) {
        return listTypedResourceGrantsByAccount(accountIds, accountIdsByGroup, accountIdsByRole, accountTypeMap,
                IamRelationTypes.SUBJECT_RESOURCE_GRANT, IamRelationTypes.TARGET_RESOURCE);
    }

    private Map<String, List<IamRelationPO>> listClientResourceGrantsByAccount(
            List<String> accountIds,
            Map<String, Set<String>> accountIdsByGroup,
            Map<String, Set<String>> accountIdsByRole,
            Map<String, String> accountTypeMap) {
        return listTypedResourceGrantsByAccount(accountIds, accountIdsByGroup, accountIdsByRole, accountTypeMap,
                IamRelationTypes.SUBJECT_CLIENT_RESOURCE_GRANT, IamRelationTypes.TARGET_CLIENT_RESOURCE);
    }

    private Map<String, List<IamRelationPO>> listTypedResourceGrantsByAccount(
            List<String> accountIds,
            Map<String, Set<String>> accountIdsByGroup,
            Map<String, Set<String>> accountIdsByRole,
            Map<String, String> accountTypeMap,
            String relationType,
            String targetType) {
        if (CollectionUtils.isEmpty(accountIds) && accountIdsByGroup.isEmpty() && accountIdsByRole.isEmpty()) {
            return Map.of();
        }
        Set<String> groupIds = accountIdsByGroup.keySet();
        Set<String> roleIds = accountIdsByRole.keySet();
        List<IamRelationPO> grants = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getRelationType, relationType)
                .eq(IamRelationPO::getTargetType, targetType)
                .and(wrapper -> wrapper.isNull(IamRelationPO::getStatus)
                        .or().eq(IamRelationPO::getStatus, IamRelationTypes.STATUS_ENABLED))
                .and(wrapper -> wrapper.isNull(IamRelationPO::getExpiredAt)
                        .or().gt(IamRelationPO::getExpiredAt, LocalDateTime.now()))
                .and(wrapper -> {
                    wrapper.and(accountWrapper -> accountWrapper
                            .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_ACCOUNT)
                            .in(IamRelationPO::getSubjectId, accountIds));
                    if (!groupIds.isEmpty()) {
                        wrapper.or(groupWrapper -> groupWrapper
                                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_GROUP)
                                .in(IamRelationPO::getSubjectId, groupIds));
                    }
                    if (!roleIds.isEmpty()) {
                        wrapper.or(roleWrapper -> roleWrapper
                                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_ROLE)
                                .in(IamRelationPO::getSubjectId, roleIds));
                    }
                }));

        Map<String, List<IamRelationPO>> byAccount = new HashMap<>();
        for (IamRelationPO grant : grants) {
            if (IamRelationTypes.SUBJECT_ACCOUNT.equals(grant.getSubjectType())
                    && accountIds.contains(grant.getSubjectId())) {
                if (matchesAccountType(accountTypeMap.get(grant.getSubjectId()), grant.getAccountType())) {
                    byAccount.computeIfAbsent(grant.getSubjectId(), key -> new ArrayList<>()).add(grant);
                }
            } else if (IamRelationTypes.SUBJECT_GROUP.equals(grant.getSubjectType())
                    && groupIds.contains(grant.getSubjectId())) {
                for (String accountId : accountIdsByGroup.getOrDefault(grant.getSubjectId(), Set.of())) {
                    if (matchesAccountType(accountTypeMap.get(accountId), grant.getAccountType())) {
                        byAccount.computeIfAbsent(accountId, key -> new ArrayList<>()).add(grant);
                    }
                }
            } else if (IamRelationTypes.SUBJECT_ROLE.equals(grant.getSubjectType())
                    && roleIds.contains(grant.getSubjectId())) {
                for (String accountId : accountIdsByRole.getOrDefault(grant.getSubjectId(), Set.of())) {
                    if (matchesAccountType(accountTypeMap.get(accountId), grant.getAccountType())) {
                        byAccount.computeIfAbsent(accountId, key -> new ArrayList<>()).add(grant);
                    }
                }
            }
        }
        return byAccount;
    }

    private Map<String, List<PermissionGrant>> listPermissionGrantsByAccount(
            Map<String, List<IamRelationPO>> resourceGrantsByAccount,
            Map<String, String> accountTypeMap) {
        Set<String> permissionResourceIds = resourceGrantsByAccount.values().stream()
                .flatMap(List::stream).map(IamRelationPO::getTargetId).collect(Collectors.toSet());
        Map<String, List<IamRelationPO>> permissionsByResource =
                loadPermissionsBySubject(IamRelationTypes.SUBJECT_RESOURCE, IamRelationTypes.RESOURCE_PERMISSION,
                        permissionResourceIds);
        Map<String, List<PermissionGrant>> result = new HashMap<>();
        for (Map.Entry<String, List<IamRelationPO>> entry : resourceGrantsByAccount.entrySet()) {
            String accountType = accountTypeMap.get(entry.getKey());
            Map<String, PermissionGrant> merged = new LinkedHashMap<>();
            for (IamRelationPO grant : entry.getValue()) {
                if (!expandsPermissionKeys(grant.getGrantMode())) {
                    continue;
                }
                for (IamRelationPO permission : permissionsByResource.getOrDefault(grant.getTargetId(), List.of())) {
                    if (!StringUtils.hasText(permission.getTargetKey())
                            || !matchesAccountType(accountType, permission.getAccountType())) {
                        continue;
                    }
                    PermissionGrant pg = new PermissionGrant();
                    pg.setPermissionKey(permission.getTargetKey());
                    pg.setDataScope(permission.getDataScope());
                    pg.setCustomScopeDeptIds(permission.getCustomScopeDeptIds() == null
                            ? List.of() : permission.getCustomScopeDeptIds());
                    pg.setSourceType(grant.getSubjectType());
                    pg.setSourceId(grant.getSubjectId());
                    merged.putIfAbsent(permission.getTargetKey(), pg);
                }
            }
            result.put(entry.getKey(), new ArrayList<>(merged.values()));
        }
        return result;
    }

    private Map<String, Set<String>> listClientPermissionKeysByAccount(
            Map<String, List<IamRelationPO>> clientResourceGrantsByAccount,
            Map<String, String> accountTypeMap) {
        Set<String> permissionResourceIds = clientResourceGrantsByAccount.values().stream()
                .flatMap(List::stream).map(IamRelationPO::getTargetId).collect(Collectors.toSet());
        Map<String, List<IamRelationPO>> permissionsByResource = loadPermissionsBySubject(
                IamRelationTypes.SUBJECT_CLIENT_RESOURCE,
                IamRelationTypes.CLIENT_RESOURCE_PERMISSION,
                permissionResourceIds);
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, List<IamRelationPO>> entry : clientResourceGrantsByAccount.entrySet()) {
            String accountType = accountTypeMap.get(entry.getKey());
            Set<String> keys = new HashSet<>();
            for (IamRelationPO grant : entry.getValue()) {
                if (!expandsPermissionKeys(grant.getGrantMode())) {
                    continue;
                }
                for (IamRelationPO permission : permissionsByResource.getOrDefault(grant.getTargetId(), List.of())) {
                    if (StringUtils.hasText(permission.getTargetKey())
                            && matchesAccountType(accountType, permission.getAccountType())) {
                        keys.add(permission.getTargetKey());
                    }
                }
            }
            result.put(entry.getKey(), keys);
        }
        return result;
    }

    private Map<String, List<IamRelationPO>> loadPermissionsBySubject(
            String subjectType, String relationType, Set<String> subjectIds) {
        Map<String, List<IamRelationPO>> permissionsByResource = new HashMap<>();
        if (CollectionUtils.isEmpty(subjectIds)) {
            return permissionsByResource;
        }
        List<IamRelationPO> permissions = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, subjectType)
                .eq(IamRelationPO::getRelationType, relationType)
                .eq(IamRelationPO::getTargetType, IamRelationTypes.TARGET_PERMISSION)
                .in(IamRelationPO::getSubjectId, subjectIds));
        for (IamRelationPO permission : permissions) {
            permissionsByResource.computeIfAbsent(permission.getSubjectId(), key -> new ArrayList<>()).add(permission);
        }
        return permissionsByResource;
    }

    private static boolean expandsPermissionKeys(String grantMode) {
        return !StringUtils.hasText(grantMode)
                || IamRelationTypes.GRANT_CASCADE.equals(grantMode)
                || IamRelationTypes.GRANT_DIRECT.equals(grantMode);
    }

    @Override
    public List<String> listTargetIds(String subjectType, String subjectId, String relationType) {
        return listTargetIds(subjectType, subjectId, relationType, null);
    }

    @Override
    public List<String> listTargetIds(String subjectType, String subjectId, String relationType, String accountType) {
        return relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                        .eq(IamRelationPO::getSubjectType, subjectType)
                        .eq(IamRelationPO::getSubjectId, subjectId)
                        .eq(IamRelationPO::getRelationType, relationType)
                        .eq(StringUtils.hasText(accountType), IamRelationPO::getAccountType, accountType))
                .stream().map(IamRelationPO::getTargetId).filter(StringUtils::hasText).distinct().toList();
    }

    @Override
    public List<String> listSubjectIds(String relationType, String targetType, String targetId) {
        return relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                        .eq(IamRelationPO::getRelationType, relationType)
                        .eq(IamRelationPO::getTargetType, targetType)
                        .eq(IamRelationPO::getTargetId, targetId))
                .stream().map(IamRelationPO::getSubjectId).filter(StringUtils::hasText).distinct().toList();
    }

    @Override
    public List<DeptGrantItem> listAccountDepts(String accountId) {
        String accountType = resolveAccountType(accountId);
        return relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                        .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_ACCOUNT)
                        .eq(IamRelationPO::getSubjectId, accountId)
                        .eq(IamRelationPO::getRelationType, IamRelationTypes.ACCOUNT_DEPT)
                        .eq(IamRelationPO::getAccountType, accountType))
                .stream()
                .map(rel -> new DeptGrantItem(rel.getTargetId(), Flags.of(rel.getIsPrimary())))
                .toList();
    }

    @Override
    public List<ResourceGrantItem> listSubjectResourceGrants(String subjectType, String subjectId) {
        return listSubjectResourceGrants(subjectType, subjectId, null);
    }

    @Override
    public List<ResourceGrantItem> listSubjectResourceGrants(String subjectType, String subjectId, String accountType) {
        return listSubjectTypedResourceGrants(subjectType, subjectId, accountType,
                IamRelationTypes.SUBJECT_RESOURCE_GRANT, IamRelationTypes.TARGET_RESOURCE,
                IamRelationTypes.SUBJECT_RESOURCE, IamRelationTypes.RESOURCE_PERMISSION);
    }

    @Override
    public List<ResourceGrantItem> listSubjectClientResourceGrants(String subjectType, String subjectId, String accountType) {
        return listSubjectTypedResourceGrants(subjectType, subjectId, accountType,
                IamRelationTypes.SUBJECT_CLIENT_RESOURCE_GRANT, IamRelationTypes.TARGET_CLIENT_RESOURCE,
                IamRelationTypes.SUBJECT_CLIENT_RESOURCE, IamRelationTypes.CLIENT_RESOURCE_PERMISSION);
    }

    private List<ResourceGrantItem> listSubjectTypedResourceGrants(
            String subjectType, String subjectId, String accountType,
            String grantRelationType, String targetType,
            String permissionSubjectType, String permissionRelationType) {
        // 1. 查主体上的资源授予边
        List<IamRelationPO> grants = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, subjectType)
                .eq(IamRelationPO::getSubjectId, subjectId)
                .eq(IamRelationPO::getRelationType, grantRelationType)
                .eq(IamRelationPO::getTargetType, targetType)
                .eq(StringUtils.hasText(accountType), IamRelationPO::getAccountType, accountType)
                .orderByAsc(IamRelationPO::getId));
        if (grants.isEmpty()) {
            return List.of();
        }
        List<String> resourceIds = grants.stream().map(IamRelationPO::getTargetId).distinct().toList();
        // 2. 批量加载各资源绑定的权限键
        Map<String, List<String>> permissionMap = new HashMap<>();
        List<IamRelationPO> permissions = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, permissionSubjectType)
                .eq(IamRelationPO::getRelationType, permissionRelationType)
                .in(IamRelationPO::getSubjectId, resourceIds)
                .eq(StringUtils.hasText(accountType), IamRelationPO::getAccountType, accountType));
        for (IamRelationPO permission : permissions) {
            permissionMap.computeIfAbsent(permission.getSubjectId(), key -> new ArrayList<>())
                    .add(permission.getTargetKey());
        }
        // 3. 加载资源元数据
        boolean client = IamRelationTypes.TARGET_CLIENT_RESOURCE.equals(targetType);
        Map<String, String> resourceTypeById = new HashMap<>();
        Map<String, String> parentIdById = new HashMap<>();
        Map<String, String> codeById = new HashMap<>();
        if (client) {
            for (ClientResourcePO resource : clientResourceMapper.selectByIds(resourceIds)) {
                resourceTypeById.put(resource.getId(), resource.getResourceType());
                parentIdById.put(resource.getId(), resource.getParentId());
                codeById.put(resource.getId(), resource.getCode());
            }
        } else {
            for (ResourcePO resource : resourceMapper.selectByIds(resourceIds)) {
                resourceTypeById.put(resource.getId(), resource.getResourceType());
                parentIdById.put(resource.getId(), resource.getParentId());
                codeById.put(resource.getId(), resource.getCode());
            }
        }
        // 4. 按钮/动作权限上卷到父菜单
        Map<String, Set<String>> grantMap = new LinkedHashMap<>();
        for (String resourceId : resourceIds) {
            String resourceType = resourceTypeById.get(resourceId);
            if ("BUTTON".equals(resourceType) || "ACTION".equals(resourceType)) {
                String parentId = StringUtils.hasText(parentIdById.get(resourceId))
                        ? parentIdById.get(resourceId) : resourceId;
                Set<String> keys = grantMap.computeIfAbsent(parentId, key -> new HashSet<>());
                List<String> mapped = permissionMap.get(resourceId);
                if (mapped != null && !mapped.isEmpty()) {
                    keys.addAll(mapped);
                } else if (StringUtils.hasText(codeById.get(resourceId))) {
                    keys.add(codeById.get(resourceId));
                }
            } else {
                grantMap.computeIfAbsent(resourceId, key -> new HashSet<>());
            }
        }
        List<ResourceGrantItem> result = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : grantMap.entrySet()) {
            result.add(new ResourceGrantItem(entry.getKey(), entry.getValue().stream().sorted().toList()));
        }
        return result;
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:account:' + #accountId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceAccountRoles(String accountId, List<String> roleIds) {
        // 1. 解析账号类型并删旧关系
        String accountType = resolveAccountType(accountId);
        deleteSubjectRelations(IamRelationTypes.SUBJECT_ACCOUNT, accountId, IamRelationTypes.ACCOUNT_ROLE, accountType);
        // 2. 空列表则踢会话返回
        if (CollectionUtils.isEmpty(roleIds)) {
            sessionPort.logoutAccount(accountId);
            return;
        }
        // 3. 重建账号-角色边
        List<IamRelationPO> relations = new ArrayList<>();
        for (String roleId : roleIds.stream().distinct().toList()) {
            relations.add(newRelation(IamRelationTypes.SUBJECT_ACCOUNT, accountId, accountType,
                    IamRelationTypes.ACCOUNT_ROLE, IamRelationTypes.TARGET_ROLE, roleId));
        }
        saveRelations(relations);
        sessionPort.logoutAccount(accountId);
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:account:' + #accountId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceAccountGroups(String accountId, List<String> groupIds) {
        String accountType = resolveAccountType(accountId);
        deleteSubjectRelations(IamRelationTypes.SUBJECT_ACCOUNT, accountId, IamRelationTypes.ACCOUNT_GROUP, accountType);
        if (CollectionUtils.isEmpty(groupIds)) {
            sessionPort.logoutAccount(accountId);
            return;
        }
        List<IamRelationPO> relations = new ArrayList<>();
        for (String groupId : groupIds.stream().distinct().toList()) {
            relations.add(newRelation(IamRelationTypes.SUBJECT_ACCOUNT, accountId, accountType,
                    IamRelationTypes.ACCOUNT_GROUP, IamRelationTypes.TARGET_GROUP, groupId));
        }
        saveRelations(relations);
        sessionPort.logoutAccount(accountId);
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:account:' + #accountId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceAccountDepts(String accountId, List<DeptGrantItem> grantInfoList) {
        String accountType = resolveAccountType(accountId);
        deleteSubjectRelations(IamRelationTypes.SUBJECT_ACCOUNT, accountId, IamRelationTypes.ACCOUNT_DEPT, accountType);
        if (CollectionUtils.isEmpty(grantInfoList)) {
            sessionPort.logoutAccount(accountId);
            return;
        }
        List<IamRelationPO> relations = new ArrayList<>();
        for (DeptGrantItem info : grantInfoList) {
            IamRelationPO rel = newRelation(IamRelationTypes.SUBJECT_ACCOUNT, accountId, accountType,
                    IamRelationTypes.ACCOUNT_DEPT, IamRelationTypes.TARGET_DEPT, info.getDeptId());
            rel.setIsPrimary(Flags.of(info.getIsPrimary()));
            relations.add(rel);
        }
        saveRelations(relations);
        sessionPort.logoutAccount(accountId);
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:' + #subjectType + ':' + #subjectId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceSubjectResourceGrants(
            String subjectType, String subjectId, List<ResourceGrantItem> grants, String accountType) {
        String resolvedType = normalizeAccountType(accountType);
        deleteSubjectRelations(subjectType, subjectId, IamRelationTypes.SUBJECT_RESOURCE_GRANT, resolvedType);
        if (CollectionUtils.isEmpty(grants)) {
            return;
        }
        List<String> originalResourceIds = grants.stream().map(ResourceGrantItem::getResourceId)
                .filter(StringUtils::hasText).distinct().toList();
        Set<String> originalSet = new HashSet<>(originalResourceIds);
        List<String> permissionKeys = grants.stream()
                .filter(item -> item.getPermissionKeys() != null)
                .flatMap(item -> item.getPermissionKeys().stream())
                .filter(StringUtils::hasText).distinct().toList();
        if (!originalResourceIds.isEmpty()) {
            Set<String> existing = resourceMapper.selectByIds(originalResourceIds).stream()
                    .map(ResourcePO::getId).collect(Collectors.toSet());
            if (existing.size() != originalResourceIds.size()) {
                throw new DomainException("Resource not found");
            }
        }
        List<String> resourceIds = new ArrayList<>(originalResourceIds);
        if (!permissionKeys.isEmpty()) {
            Map<String, Set<String>> permissionResourceMap = resolvePermissionResourceIds(
                    permissionKeys, resolvedType,
                    IamRelationTypes.SUBJECT_RESOURCE, IamRelationTypes.RESOURCE_PERMISSION, false);
            for (String permissionKey : permissionKeys) {
                Set<String> ids = permissionResourceMap.get(permissionKey);
                if (ids == null || ids.isEmpty()) {
                    throw new DomainException("Permission resource not found: " + permissionKey);
                }
                resourceIds.addAll(ids);
            }
        }
        resourceIds = resourceIds.stream().filter(StringUtils::hasText).distinct().toList();
        List<IamRelationPO> relations = new ArrayList<>(resourceIds.size());
        for (String resourceId : resourceIds) {
            boolean cascadeWhole = permissionKeys.isEmpty() && originalSet.contains(resourceId);
            boolean permissionEdge = !originalSet.contains(resourceId);
            IamRelationPO rel = newRelation(subjectType, subjectId, resolvedType,
                    IamRelationTypes.SUBJECT_RESOURCE_GRANT, IamRelationTypes.TARGET_RESOURCE, resourceId);
            rel.setGrantMode(cascadeWhole || permissionEdge
                    ? IamRelationTypes.GRANT_CASCADE : IamRelationTypes.GRANT_DIRECT);
            relations.add(rel);
        }
        saveRelations(relations);
        if (IamRelationTypes.SUBJECT_ACCOUNT.equals(subjectType)) {
            sessionPort.logoutAccount(subjectId);
        }
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:client:' + #subjectType + ':' + #subjectId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceSubjectClientResourceGrants(
            String subjectType, String subjectId, List<ResourceGrantItem> grants, String accountType) {
        String resolvedType = normalizeAccountType(accountType);
        deleteSubjectRelations(subjectType, subjectId, IamRelationTypes.SUBJECT_CLIENT_RESOURCE_GRANT, resolvedType);
        if (CollectionUtils.isEmpty(grants)) {
            return;
        }
        List<String> originalResourceIds = grants.stream().map(ResourceGrantItem::getResourceId)
                .filter(StringUtils::hasText).distinct().toList();
        Set<String> originalSet = new HashSet<>(originalResourceIds);
        List<String> permissionKeys = grants.stream()
                .filter(item -> item.getPermissionKeys() != null)
                .flatMap(item -> item.getPermissionKeys().stream())
                .filter(StringUtils::hasText).distinct().toList();
        if (!originalResourceIds.isEmpty()) {
            Set<String> existing = clientResourceMapper.selectByIds(originalResourceIds).stream()
                    .map(ClientResourcePO::getId).collect(Collectors.toSet());
            if (existing.size() != originalResourceIds.size()) {
                throw new DomainException("Client resource not found");
            }
        }
        List<String> resourceIds = new ArrayList<>(originalResourceIds);
        if (!permissionKeys.isEmpty()) {
            Map<String, Set<String>> permissionResourceMap = resolvePermissionResourceIds(
                    permissionKeys, resolvedType,
                    IamRelationTypes.SUBJECT_CLIENT_RESOURCE, IamRelationTypes.CLIENT_RESOURCE_PERMISSION, true);
            for (String permissionKey : permissionKeys) {
                Set<String> ids = permissionResourceMap.get(permissionKey);
                if (ids == null || ids.isEmpty()) {
                    throw new DomainException("Client permission resource not found: " + permissionKey);
                }
                resourceIds.addAll(ids);
            }
        }
        resourceIds = resourceIds.stream().filter(StringUtils::hasText).distinct().toList();
        List<IamRelationPO> relations = new ArrayList<>(resourceIds.size());
        for (String resourceId : resourceIds) {
            boolean cascadeWhole = permissionKeys.isEmpty() && originalSet.contains(resourceId);
            boolean permissionEdge = !originalSet.contains(resourceId);
            IamRelationPO rel = newRelation(subjectType, subjectId, resolvedType,
                    IamRelationTypes.SUBJECT_CLIENT_RESOURCE_GRANT, IamRelationTypes.TARGET_CLIENT_RESOURCE, resourceId);
            rel.setGrantMode(cascadeWhole || permissionEdge
                    ? IamRelationTypes.GRANT_CASCADE : IamRelationTypes.GRANT_DIRECT);
            relations.add(rel);
        }
        saveRelations(relations);
        if (IamRelationTypes.SUBJECT_ACCOUNT.equals(subjectType)) {
            sessionPort.logoutAccount(subjectId);
        }
    }

    private Map<String, Set<String>> resolvePermissionResourceIds(
            List<String> permissionKeys, String accountType,
            String permissionSubjectType, String permissionRelationType, boolean client) {
        Map<String, Set<String>> permissionResourceMap = new HashMap<>();
        List<IamRelationPO> permissionRows = relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, permissionSubjectType)
                .eq(IamRelationPO::getRelationType, permissionRelationType)
                .eq(IamRelationPO::getTargetType, IamRelationTypes.TARGET_PERMISSION)
                .eq(IamRelationPO::getAccountType, accountType)
                .in(IamRelationPO::getTargetKey, permissionKeys));
        for (IamRelationPO row : permissionRows) {
            if (StringUtils.hasText(row.getTargetKey()) && StringUtils.hasText(row.getSubjectId())) {
                permissionResourceMap.computeIfAbsent(row.getTargetKey(), key -> new HashSet<>()).add(row.getSubjectId());
            }
        }
        if (client) {
            clientResourceMapper.selectList(Wrappers.<ClientResourcePO>lambdaQuery()
                            .in(ClientResourcePO::getCode, permissionKeys)
                            .in(ClientResourcePO::getResourceType, List.of("BUTTON", "ACTION")))
                    .forEach(resource -> permissionResourceMap
                            .computeIfAbsent(resource.getCode(), key -> new HashSet<>()).add(resource.getId()));
        } else {
            resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                            .in(ResourcePO::getCode, permissionKeys)
                            .in(ResourcePO::getResourceType, List.of("BUTTON", "ACTION")))
                    .forEach(resource -> permissionResourceMap
                            .computeIfAbsent(resource.getCode(), key -> new HashSet<>()).add(resource.getId()));
        }
        return permissionResourceMap;
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:role:' + #roleId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceRoleUsers(String roleId, List<String> accountIds) {
        if (roleMapper.selectById(roleId) == null) {
            throw new DomainException(404, "Role not found");
        }
        Set<String> affected = new HashSet<>(listSubjectIdsByTarget(
                IamRelationTypes.ACCOUNT_ROLE, IamRelationTypes.TARGET_ROLE, roleId));
        if (accountIds != null) {
            accountIds.stream().filter(StringUtils::hasText).forEach(affected::add);
        }
        relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getRelationType, IamRelationTypes.ACCOUNT_ROLE)
                .eq(IamRelationPO::getTargetType, IamRelationTypes.TARGET_ROLE)
                .eq(IamRelationPO::getTargetId, roleId));
        if (!CollectionUtils.isEmpty(accountIds)) {
            Map<String, String> accountTypeMap = loadAccountTypeMap(accountIds);
            List<IamRelationPO> relations = new ArrayList<>();
            for (String accountId : accountIds.stream().distinct().toList()) {
                String accountType = accountTypeMap.get(accountId);
                if (!StringUtils.hasText(accountType)) {
                    throw new DomainException("Account not found: " + accountId);
                }
                relations.add(newRelation(IamRelationTypes.SUBJECT_ACCOUNT, accountId, accountType,
                        IamRelationTypes.ACCOUNT_ROLE, IamRelationTypes.TARGET_ROLE, roleId));
            }
            saveRelations(relations);
        }
        logoutAccounts(affected);
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:group:' + #groupId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceGroupUsers(String groupId, List<String> accountIds) {
        Set<String> affected = new HashSet<>(listSubjectIdsByTarget(
                IamRelationTypes.ACCOUNT_GROUP, IamRelationTypes.TARGET_GROUP, groupId));
        if (accountIds != null) {
            accountIds.stream().filter(StringUtils::hasText).forEach(affected::add);
        }
        relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getRelationType, IamRelationTypes.ACCOUNT_GROUP)
                .eq(IamRelationPO::getTargetType, IamRelationTypes.TARGET_GROUP)
                .eq(IamRelationPO::getTargetId, groupId));
        if (!CollectionUtils.isEmpty(accountIds)) {
            Map<String, String> accountTypeMap = loadAccountTypeMap(accountIds);
            List<IamRelationPO> relations = new ArrayList<>();
            for (String accountId : accountIds.stream().distinct().toList()) {
                String accountType = accountTypeMap.get(accountId);
                if (!StringUtils.hasText(accountType)) {
                    throw new DomainException("Account not found: " + accountId);
                }
                relations.add(newRelation(IamRelationTypes.SUBJECT_ACCOUNT, accountId, accountType,
                        IamRelationTypes.ACCOUNT_GROUP, IamRelationTypes.TARGET_GROUP, groupId));
            }
            saveRelations(relations);
        }
        logoutAccounts(affected);
    }

    @Override
    @Transactional
    @Lock4j(keys = {"'iam:rel:group-roles:' + #groupId"}, expire = 30000, acquireTimeout = 5000)
    public void replaceGroupRoles(String groupId, List<String> roleIds, String accountType) {
        String resolvedType = normalizeAccountType(accountType);
        Set<String> affected = new HashSet<>(listSubjectIdsByTarget(
                IamRelationTypes.ACCOUNT_GROUP, IamRelationTypes.TARGET_GROUP, groupId));
        deleteSubjectRelations(IamRelationTypes.SUBJECT_GROUP, groupId, IamRelationTypes.GROUP_ROLE, resolvedType);
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<IamRelationPO> relations = new ArrayList<>();
            for (String roleId : roleIds.stream().distinct().toList()) {
                relations.add(newRelation(IamRelationTypes.SUBJECT_GROUP, groupId, resolvedType,
                        IamRelationTypes.GROUP_ROLE, IamRelationTypes.TARGET_ROLE, roleId));
            }
            saveRelations(relations);
        }
        logoutAccounts(affected);
    }

    private List<String> listSubjectIdsByTarget(String relationType, String targetType, String targetId) {
        return relationMapper.selectList(Wrappers.<IamRelationPO>lambdaQuery()
                        .eq(IamRelationPO::getRelationType, relationType)
                        .eq(IamRelationPO::getTargetType, targetType)
                        .eq(IamRelationPO::getTargetId, targetId)
                        .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_ACCOUNT))
                .stream().map(IamRelationPO::getSubjectId).filter(StringUtils::hasText).distinct().toList();
    }

    private void logoutAccounts(Collection<String> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) {
            return;
        }
        for (String accountId : accountIds) {
            if (StringUtils.hasText(accountId)) {
                sessionPort.logoutAccount(accountId);
            }
        }
    }

    @Override
    @Transactional
    public void bindResourcePermission(String resourceId, String permissionKey, String accountType,
                                       String dataScope, List<String> customScopeDeptIds,
                                       Integer sort, String description) {
        String resolvedType = normalizeAccountType(accountType);
        relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_RESOURCE)
                .eq(IamRelationPO::getSubjectId, resourceId)
                .eq(IamRelationPO::getRelationType, IamRelationTypes.RESOURCE_PERMISSION)
                .eq(IamRelationPO::getTargetKey, permissionKey)
                .eq(IamRelationPO::getAccountType, resolvedType));
        IamRelationPO rel = newRelation(IamRelationTypes.SUBJECT_RESOURCE, resourceId, resolvedType,
                IamRelationTypes.RESOURCE_PERMISSION, IamRelationTypes.TARGET_PERMISSION, null);
        rel.setTargetKey(permissionKey);
        rel.setDataScope(dataScope);
        rel.setCustomScopeDeptIds(customScopeDeptIds);
        rel.setSort(sort);
        rel.setDescription(description);
        relationMapper.insert(rel);
    }

    @Override
    @Transactional
    public void bindClientResourcePermission(String resourceId, String permissionKey, String accountType,
                                             String dataScope, List<String> customScopeDeptIds,
                                             Integer sort, String description) {
        String resolvedType = normalizeAccountType(accountType);
        relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, IamRelationTypes.SUBJECT_CLIENT_RESOURCE)
                .eq(IamRelationPO::getSubjectId, resourceId)
                .eq(IamRelationPO::getRelationType, IamRelationTypes.CLIENT_RESOURCE_PERMISSION)
                .eq(IamRelationPO::getTargetKey, permissionKey)
                .eq(IamRelationPO::getAccountType, resolvedType));
        IamRelationPO rel = newRelation(IamRelationTypes.SUBJECT_CLIENT_RESOURCE, resourceId, resolvedType,
                IamRelationTypes.CLIENT_RESOURCE_PERMISSION, IamRelationTypes.TARGET_PERMISSION, null);
        rel.setTargetKey(permissionKey);
        rel.setDataScope(dataScope);
        rel.setCustomScopeDeptIds(customScopeDeptIds);
        rel.setSort(sort);
        rel.setDescription(description);
        relationMapper.insert(rel);
    }

    @Override
    public void deleteSubjectRelations(String subjectType, String subjectId, String relationType) {
        deleteSubjectRelations(subjectType, subjectId, relationType, null);
    }

    @Override
    public void deleteSubjectRelations(String subjectType, String subjectId, String relationType, String accountType) {
        relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                .eq(IamRelationPO::getSubjectType, subjectType)
                .eq(IamRelationPO::getSubjectId, subjectId)
                .eq(IamRelationPO::getRelationType, relationType)
                .eq(StringUtils.hasText(accountType), IamRelationPO::getAccountType, accountType));
    }

    @Override
    public void deleteSubjectRelations(String subjectType, Collection<String> subjectIds, String relationType) {
        if (CollectionUtils.isEmpty(subjectIds)) {
            return;
        }
        for (List<String> batch : BatchPartition.partition(subjectIds)) {
            relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                    .eq(IamRelationPO::getSubjectType, subjectType)
                    .eq(IamRelationPO::getRelationType, relationType)
                    .in(IamRelationPO::getSubjectId, batch));
        }
    }

    @Override
    public void deleteBySubjectIds(String subjectType, Collection<String> subjectIds) {
        if (CollectionUtils.isEmpty(subjectIds)) {
            return;
        }
        for (List<String> batch : BatchPartition.partition(subjectIds)) {
            relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                    .eq(IamRelationPO::getSubjectType, subjectType)
                    .in(IamRelationPO::getSubjectId, batch));
        }
    }

    @Override
    public void deleteByTargetIds(String targetType, Collection<String> targetIds) {
        if (CollectionUtils.isEmpty(targetIds)) {
            return;
        }
        for (List<String> batch : BatchPartition.partition(targetIds)) {
            relationMapper.delete(Wrappers.<IamRelationPO>lambdaQuery()
                    .eq(IamRelationPO::getTargetType, targetType)
                    .in(IamRelationPO::getTargetId, batch));
        }
    }

    private void saveRelations(List<IamRelationPO> relations) {
        if (relations == null || relations.isEmpty()) {
            return;
        }
        for (List<IamRelationPO> batch : BatchPartition.partition(relations, 1000)) {
            for (IamRelationPO rel : batch) {
                relationMapper.insert(rel);
            }
        }
    }

    private IamRelationPO newRelation(String subjectType, String subjectId, String accountType,
                                      String relationType, String targetType, String targetId) {
        IamRelationPO rel = new IamRelationPO();
        rel.setSubjectType(subjectType);
        rel.setSubjectId(subjectId);
        rel.setAccountType(normalizeAccountType(accountType));
        rel.setRelationType(relationType);
        rel.setTargetType(targetType);
        rel.setTargetId(targetId);
        rel.setStatus(IamRelationTypes.STATUS_ENABLED);
        return rel;
    }

    private Map<String, String> loadAccountTypeMap(Collection<String> accountIds) {
        List<String> uniqueIds = accountIds.stream().filter(StringUtils::hasText).distinct().toList();
        if (uniqueIds.isEmpty()) {
            return Map.of();
        }
        Map<String, String> map = new HashMap<>();
        for (AccountPO account : accountMapper.selectByIds(uniqueIds)) {
            map.put(account.getId(), account.getAccountType());
        }
        return map;
    }

    private String resolveAccountType(String accountId) {
        AccountPO account = accountMapper.selectById(accountId);
        if (account == null || !StringUtils.hasText(account.getAccountType())) {
            throw new DomainException(404, "Account not found");
        }
        return normalizeAccountType(account.getAccountType());
    }

    private static String normalizeAccountType(String accountType) {
        if (!StringUtils.hasText(accountType)) {
            return "ADMIN";
        }
        return accountType.trim().toUpperCase();
    }

    private static boolean matchesAccountType(String expected, String actual) {
        if (!StringUtils.hasText(expected)) {
            return false;
        }
        return expected.equalsIgnoreCase(actual);
    }
}

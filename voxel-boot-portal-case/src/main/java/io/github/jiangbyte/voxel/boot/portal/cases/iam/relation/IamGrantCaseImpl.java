package io.github.jiangbyte.voxel.boot.portal.cases.iam.relation;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.repository.AccountRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.repository.GroupRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.repository.RoleRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.DeptGrantItem;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.IamRelationTypes;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantCatalogPort;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantItem;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import lombok.RequiredArgsConstructor;

/**
 * IAM 授权用例：账号/角色/用户组的 own-* 与 grant-*。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class IamGrantCaseImpl implements IIamGrantCase {

    private final IamRelationService relationService;
    private final ResourceGrantCatalogPort grantCatalogPort;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;


    // ---------- Account ----------

    /**
     * 查询账号已拥有角色。
     */
    public Map<String, Object> ownAccountRoles(String accountId) {
        // 1. 校验账号存在
        Account account = requireAccount(accountId);
        // 2. 查关系目标 id
        List<String> roleIds = relationService.listTargetIds(
                IamRelationTypes.SUBJECT_ACCOUNT, accountId, IamRelationTypes.ACCOUNT_ROLE,
                account.getAccountType().name());
        // 3. 组装结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", accountId);
        result.put("roleIds", roleIds);
        result.put("roles", List.of());
        return result;
    }

    @Transactional
    public void grantAccountRoles(String accountId, List<String> roleIds) {
        requireAccount(accountId);
        relationService.replaceAccountRoles(accountId, roleIds);
    }

    public Map<String, Object> ownAccountGroups(String accountId) {
        Account account = requireAccount(accountId);
        List<String> groupIds = relationService.listTargetIds(
                IamRelationTypes.SUBJECT_ACCOUNT, accountId, IamRelationTypes.ACCOUNT_GROUP,
                account.getAccountType().name());
        Map<String, Object> result = new HashMap<>();
        result.put("id", accountId);
        result.put("groupIds", groupIds);
        result.put("groups", List.of());
        return result;
    }

    @Transactional
    public void grantAccountGroups(String accountId, List<String> groupIds) {
        requireAccount(accountId);
        relationService.replaceAccountGroups(accountId, groupIds);
    }

    public OwnDeptView ownAccountDepts(String accountId) {
        requireAccount(accountId);
        return new OwnDeptView(accountId, relationService.listAccountDepts(accountId));
    }

    @Transactional
    public void grantAccountDepts(String accountId, List<DeptGrantItem> grantInfoList) {
        requireAccount(accountId);
        relationService.replaceAccountDepts(accountId, grantInfoList);
    }

    public ResourceOwnView ownAccountResources(String accountId) {
        Account account = requireAccount(accountId);
        ResourceOwnView view = new ResourceOwnView();
        view.setId(accountId);
        view.setModules(grantCatalogPort.listAdminGrantModules(account.getAccountType().name()));
        view.setGrantInfoList(relationService.listSubjectResourceGrants(
                IamRelationTypes.SUBJECT_ACCOUNT, accountId, account.getAccountType().name()));
        return view;
    }

    @Transactional
    public void grantAccountResources(String accountId, List<ResourceGrantItem> grants) {
        Account account = requireAccount(accountId);
        relationService.replaceSubjectResourceGrants(
                IamRelationTypes.SUBJECT_ACCOUNT, accountId, grants, account.getAccountType().name());
    }

    public ResourceOwnView ownAccountClientResources(String accountId) {
        Account account = requireAccount(accountId);
        ResourceOwnView view = new ResourceOwnView();
        view.setId(accountId);
        view.setModules(grantCatalogPort.listClientGrantModules(account.getAccountType().name()));
        view.setGrantInfoList(relationService.listSubjectClientResourceGrants(
                IamRelationTypes.SUBJECT_ACCOUNT, accountId, account.getAccountType().name()));
        return view;
    }

    @Transactional
    public void grantAccountClientResources(String accountId, List<ResourceGrantItem> grants) {
        Account account = requireAccount(accountId);
        relationService.replaceSubjectClientResourceGrants(
                IamRelationTypes.SUBJECT_ACCOUNT, accountId, grants, account.getAccountType().name());
    }

    /**
     * 更新账号邮箱/手机登录身份。
     */
    @Transactional
    public void updateLoginIdentity(String accountId, Integer emailLoginEnabled, String email,
                                    Integer phoneLoginEnabled, String phone) {
        // 1. 加载并校验账号
        Account account = requireAccount(accountId);
        if (account.getAccountStatus() == AccountStatus.CANCELLED) {
            throw new DomainException("已注销账号不允许通过管理端修改");
        }
        // 2. 启用时校验对应联系方式非空
        if (java.util.Objects.equals(emailLoginEnabled, 1) && !StringUtils.hasText(email)) {
            throw new DomainException("Email login requires an email");
        }
        if (java.util.Objects.equals(phoneLoginEnabled, 1) && !StringUtils.hasText(phone)) {
            throw new DomainException("Phone login requires a phone");
        }
        // 3. 唯一性校验
        if (java.util.Objects.equals(emailLoginEnabled, 1) && StringUtils.hasText(email)) {
            ensureIdentifierUnique(email.trim().toLowerCase(Locale.ROOT), "EMAIL", accountId);
        }
        if (java.util.Objects.equals(phoneLoginEnabled, 1) && StringUtils.hasText(phone)) {
            ensureIdentifierUnique(phone.trim(), "PHONE", accountId);
        }
        // 4. 聚合内替换次级身份并保存
        account.replaceSecondaryLoginIdentities(emailLoginEnabled, email, phoneLoginEnabled, phone);
        accountRepository.save(account);
    }

    // ---------- Role ----------

    public ResourceOwnView ownRoleResources(String roleId, String accountType) {
        requireRole(roleId);
        String type = normalize(accountType);
        ResourceOwnView view = new ResourceOwnView();
        view.setId(roleId);
        view.setModules(grantCatalogPort.listAdminGrantModules(type));
        view.setGrantInfoList(relationService.listSubjectResourceGrants(
                IamRelationTypes.SUBJECT_ROLE, roleId, type));
        return view;
    }

    @Transactional
    public void grantRoleResources(String roleId, String accountType, List<ResourceGrantItem> grants) {
        requireRole(roleId);
        relationService.replaceSubjectResourceGrants(
                IamRelationTypes.SUBJECT_ROLE, roleId, grants, normalize(accountType));
    }

    public ResourceOwnView ownRoleClientResources(String roleId, String accountType) {
        requireRole(roleId);
        String type = normalize(accountType);
        ResourceOwnView view = new ResourceOwnView();
        view.setId(roleId);
        view.setModules(grantCatalogPort.listClientGrantModules(type));
        view.setGrantInfoList(relationService.listSubjectClientResourceGrants(
                IamRelationTypes.SUBJECT_ROLE, roleId, type));
        return view;
    }

    @Transactional
    public void grantRoleClientResources(String roleId, String accountType, List<ResourceGrantItem> grants) {
        requireRole(roleId);
        relationService.replaceSubjectClientResourceGrants(
                IamRelationTypes.SUBJECT_ROLE, roleId, grants, normalize(accountType));
    }

    public OwnUserView ownRoleUsers(String roleId) {
        requireRole(roleId);
        List<String> accountIds = relationService.listSubjectIds(
                IamRelationTypes.ACCOUNT_ROLE, IamRelationTypes.TARGET_ROLE, roleId);
        return new OwnUserView(roleId, accountIds);
    }

    @Transactional
    public void grantRoleUsers(String roleId, List<String> accountIds) {
        requireRole(roleId);
        relationService.replaceRoleUsers(roleId, accountIds);
    }

    // ---------- Group ----------

    public OwnUserView ownGroupUsers(String groupId) {
        requireGroup(groupId);
        List<String> accountIds = relationService.listSubjectIds(
                IamRelationTypes.ACCOUNT_GROUP, IamRelationTypes.TARGET_GROUP, groupId);
        return new OwnUserView(groupId, accountIds);
    }

    @Transactional
    public void grantGroupUsers(String groupId, List<String> accountIds) {
        requireGroup(groupId);
        relationService.replaceGroupUsers(groupId, accountIds);
    }

    public Map<String, Object> ownGroupRoles(String groupId, String accountType) {
        requireGroup(groupId);
        List<String> roleIds = relationService.listTargetIds(
                IamRelationTypes.SUBJECT_GROUP, groupId, IamRelationTypes.GROUP_ROLE, normalize(accountType));
        Map<String, Object> result = new HashMap<>();
        result.put("id", groupId);
        result.put("roleIds", roleIds);
        return result;
    }

    @Transactional
    public void grantGroupRoles(String groupId, String accountType, List<String> roleIds) {
        requireGroup(groupId);
        relationService.replaceGroupRoles(groupId, roleIds, normalize(accountType));
    }

    public ResourceOwnView ownGroupResources(String groupId, String accountType) {
        requireGroup(groupId);
        String type = normalize(accountType);
        ResourceOwnView view = new ResourceOwnView();
        view.setId(groupId);
        view.setModules(grantCatalogPort.listAdminGrantModules(type));
        view.setGrantInfoList(relationService.listSubjectResourceGrants(
                IamRelationTypes.SUBJECT_GROUP, groupId, type));
        return view;
    }

    @Transactional
    public void grantGroupResources(String groupId, String accountType, List<ResourceGrantItem> grants) {
        requireGroup(groupId);
        relationService.replaceSubjectResourceGrants(
                IamRelationTypes.SUBJECT_GROUP, groupId, grants, normalize(accountType));
    }

    public ResourceOwnView ownGroupClientResources(String groupId, String accountType) {
        requireGroup(groupId);
        String type = normalize(accountType);
        ResourceOwnView view = new ResourceOwnView();
        view.setId(groupId);
        view.setModules(grantCatalogPort.listClientGrantModules(type));
        view.setGrantInfoList(relationService.listSubjectClientResourceGrants(
                IamRelationTypes.SUBJECT_GROUP, groupId, type));
        return view;
    }

    @Transactional
    public void grantGroupClientResources(String groupId, String accountType, List<ResourceGrantItem> grants) {
        requireGroup(groupId);
        relationService.replaceSubjectClientResourceGrants(
                IamRelationTypes.SUBJECT_GROUP, groupId, grants, normalize(accountType));
    }

    private Account requireAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new DomainException(404, "Account not found"));
    }

    private void requireRole(String roleId) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new DomainException(404, "Role not found"));
    }

    private void requireGroup(String groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new DomainException(404, "Group not found"));
    }

    private void ensureIdentifierUnique(String identifier, String identityType, String excludeAccountId) {
        accountRepository.findByIdentifier(identifier, identityType).ifPresent(existing -> {
            if (excludeAccountId == null || !excludeAccountId.equals(existing.getId())) {
                throw new DomainException("登录标识已存在");
            }
        });
    }

    private static String normalize(String accountType) {
        return StringUtils.hasText(accountType) ? accountType.trim().toUpperCase(Locale.ROOT) : "ADMIN";
    }
}

package io.github.jiangbyte.voxel.boot.portal.cases.iam.relation;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.repository.AccountRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.repository.GroupRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.repository.RoleRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
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

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IIamGrantCase {
    Map<String, Object> ownAccountRoles(String accountId);
    void grantAccountRoles(String accountId, List<String> roleIds);
    Map<String, Object> ownAccountGroups(String accountId);
    void grantAccountGroups(String accountId, List<String> groupIds);
    OwnDeptView ownAccountDepts(String accountId);
    void grantAccountDepts(String accountId, List<DeptGrantItem> grantInfoList);
    ResourceOwnView ownAccountResources(String accountId);
    void grantAccountResources(String accountId, List<ResourceGrantItem> grants);
    ResourceOwnView ownAccountClientResources(String accountId);
    void grantAccountClientResources(String accountId, List<ResourceGrantItem> grants);
    void updateLoginIdentity(String accountId, Integer emailLoginEnabled, String email, Integer phoneLoginEnabled, String phone);
    ResourceOwnView ownRoleResources(String roleId, String accountType);
    void grantRoleResources(String roleId, String accountType, List<ResourceGrantItem> grants);
    ResourceOwnView ownRoleClientResources(String roleId, String accountType);
    void grantRoleClientResources(String roleId, String accountType, List<ResourceGrantItem> grants);
    OwnUserView ownRoleUsers(String roleId);
    void grantRoleUsers(String roleId, List<String> accountIds);
    OwnUserView ownGroupUsers(String groupId);
    void grantGroupUsers(String groupId, List<String> accountIds);
    Map<String, Object> ownGroupRoles(String groupId, String accountType);
    void grantGroupRoles(String groupId, String accountType, List<String> roleIds);
    ResourceOwnView ownGroupResources(String groupId, String accountType);
    void grantGroupResources(String groupId, String accountType, List<ResourceGrantItem> grants);
    ResourceOwnView ownGroupClientResources(String groupId, String accountType);
    void grantGroupClientResources(String groupId, String accountType, List<ResourceGrantItem> grants);
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IAM 关系图端口：授权聚合、查询与全量替换授予。
 *
 * Author: Charlie
 */
public interface IamRelationService {

    AccountAuthorization getAccountAuthorization(String accountId);

    Map<String, AccountAuthorization> getAccountsAuthorization(Collection<String> accountIds);

    Map<String, AccountAuthorization> getAccountsMembership(Collection<String> accountIds);

    List<String> listTargetIds(String subjectType, String subjectId, String relationType);

    List<String> listTargetIds(String subjectType, String subjectId, String relationType, String accountType);

    List<String> listSubjectIds(String relationType, String targetType, String targetId);

    List<DeptGrantItem> listAccountDepts(String accountId);

    List<ResourceGrantItem> listSubjectResourceGrants(String subjectType, String subjectId);

    List<ResourceGrantItem> listSubjectResourceGrants(String subjectType, String subjectId, String accountType);

    List<ResourceGrantItem> listSubjectClientResourceGrants(String subjectType, String subjectId, String accountType);

    void replaceAccountRoles(String accountId, List<String> roleIds);

    void replaceAccountGroups(String accountId, List<String> groupIds);

    void replaceAccountDepts(String accountId, List<DeptGrantItem> grantInfoList);

    void replaceSubjectResourceGrants(String subjectType, String subjectId, List<ResourceGrantItem> grants, String accountType);

    void replaceSubjectClientResourceGrants(String subjectType, String subjectId, List<ResourceGrantItem> grants, String accountType);

    void replaceRoleUsers(String roleId, List<String> accountIds);

    void replaceGroupUsers(String groupId, List<String> accountIds);

    void replaceGroupRoles(String groupId, List<String> roleIds, String accountType);

    void bindResourcePermission(String resourceId, String permissionKey, String accountType,
                                String dataScope, List<String> customScopeDeptIds, Integer sort, String description);

    void bindClientResourcePermission(String resourceId, String permissionKey, String accountType,
                                      String dataScope, List<String> customScopeDeptIds, Integer sort, String description);

    void deleteSubjectRelations(String subjectType, String subjectId, String relationType);

    void deleteSubjectRelations(String subjectType, String subjectId, String relationType, String accountType);

    void deleteSubjectRelations(String subjectType, Collection<String> subjectIds, String relationType);

    void deleteBySubjectIds(String subjectType, Collection<String> subjectIds);

    void deleteByTargetIds(String targetType, Collection<String> targetIds);
}

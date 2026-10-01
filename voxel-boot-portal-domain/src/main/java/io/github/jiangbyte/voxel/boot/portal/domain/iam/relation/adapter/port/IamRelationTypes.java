package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

/**
 * IAM 关系类型/主体/目标/授予模式等常量。
 *
 * Author: Charlie
 */
public final class IamRelationTypes {
    public static final String ACCOUNT_ROLE = "ACCOUNT_ROLE";
    public static final String ACCOUNT_DEPT = "ACCOUNT_DEPT";
    public static final String ACCOUNT_GROUP = "ACCOUNT_GROUP";
    public static final String GROUP_ROLE = "GROUP_ROLE";
    public static final String SUBJECT_RESOURCE_GRANT = "SUBJECT_RESOURCE_GRANT";
    public static final String RESOURCE_PERMISSION = "RESOURCE_PERMISSION";
    public static final String SUBJECT_CLIENT_RESOURCE_GRANT = "SUBJECT_CLIENT_RESOURCE_GRANT";
    public static final String CLIENT_RESOURCE_PERMISSION = "CLIENT_RESOURCE_PERMISSION";

    public static final String SUBJECT_ACCOUNT = "ACCOUNT";
    public static final String SUBJECT_GROUP = "GROUP";
    public static final String SUBJECT_ROLE = "ROLE";
    public static final String SUBJECT_RESOURCE = "RESOURCE";
    public static final String SUBJECT_CLIENT_RESOURCE = "CLIENT_RESOURCE";

    public static final String TARGET_ROLE = "ROLE";
    public static final String TARGET_DEPT = "DEPT";
    public static final String TARGET_GROUP = "GROUP";
    public static final String TARGET_RESOURCE = "RESOURCE";
    public static final String TARGET_CLIENT_RESOURCE = "CLIENT_RESOURCE";
    public static final String TARGET_PERMISSION = "PERMISSION";

    public static final String GRANT_DIRECT = "DIRECT";
    public static final String GRANT_CASCADE = "CASCADE";
    public static final String STATUS_ENABLED = "ENABLED";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    private IamRelationTypes() {
    }
}

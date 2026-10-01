package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.List;

/**
 * 权限注册表：扫描 @SaCheckPermission 并缓存。
 *
 * Author: Charlie
 */
public interface PermissionRegistryService {
    void syncToRedis();
    List<RegisteredPermissionView> listRegisteredPermissions();
    void ensureRegistered(String permissionKey);
}

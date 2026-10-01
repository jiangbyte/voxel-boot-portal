package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 账号授权聚合视图：角色/部门/组/资源与权限键。
 *
 * Author: Charlie
 */
public class AccountAuthorization {
    private List<String> roleIds = new ArrayList<>();
    private List<String> roleCodes = new ArrayList<>();
    private List<String> groupIds = new ArrayList<>();
    private List<String> deptIds = new ArrayList<>();
    private List<String> resourceIds = new ArrayList<>();
    private List<String> clientResourceIds = new ArrayList<>();
    private List<String> permissionKeys = new ArrayList<>();
    private List<String> clientPermissionKeys = new ArrayList<>();
    private List<String> buttonCodes = new ArrayList<>();
    private List<PermissionGrant> permissionGrants = new ArrayList<>();

    public Set<String> permissionSet() {
        return new LinkedHashSet<>(permissionKeys == null ? List.of() : permissionKeys);
    }

    public Set<String> roleSet() {
        return new LinkedHashSet<>(roleCodes == null ? List.of() : roleCodes);
    }

    public List<String> getRoleIds() { return roleIds; }
    public void setRoleIds(List<String> roleIds) { this.roleIds = roleIds; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
    public List<String> getGroupIds() { return groupIds; }
    public void setGroupIds(List<String> groupIds) { this.groupIds = groupIds; }
    public List<String> getDeptIds() { return deptIds; }
    public void setDeptIds(List<String> deptIds) { this.deptIds = deptIds; }
    public List<String> getResourceIds() { return resourceIds; }
    public void setResourceIds(List<String> resourceIds) { this.resourceIds = resourceIds; }
    public List<String> getClientResourceIds() { return clientResourceIds; }
    public void setClientResourceIds(List<String> clientResourceIds) { this.clientResourceIds = clientResourceIds; }
    public List<String> getPermissionKeys() { return permissionKeys; }
    public void setPermissionKeys(List<String> permissionKeys) { this.permissionKeys = permissionKeys; }
    public List<String> getClientPermissionKeys() { return clientPermissionKeys; }
    public void setClientPermissionKeys(List<String> clientPermissionKeys) { this.clientPermissionKeys = clientPermissionKeys; }
    public List<String> getButtonCodes() { return buttonCodes; }
    public void setButtonCodes(List<String> buttonCodes) { this.buttonCodes = buttonCodes; }
    public List<PermissionGrant> getPermissionGrants() { return permissionGrants; }
    public void setPermissionGrants(List<PermissionGrant> permissionGrants) { this.permissionGrants = permissionGrants; }
}

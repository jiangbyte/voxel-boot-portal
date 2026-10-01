package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.ArrayList;
import java.util.List;

/**
 * 主体资源授予明细。
 *
 * Author: Charlie
 */
public class ResourceGrantItem {
    private String resourceId;
    private List<String> permissionKeys = new ArrayList<>();

    public ResourceGrantItem() {}
    public ResourceGrantItem(String resourceId, List<String> permissionKeys) {
        this.resourceId = resourceId;
        this.permissionKeys = permissionKeys == null ? new ArrayList<>() : permissionKeys;
    }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public List<String> getPermissionKeys() { return permissionKeys; }
    public void setPermissionKeys(List<String> permissionKeys) { this.permissionKeys = permissionKeys; }
}

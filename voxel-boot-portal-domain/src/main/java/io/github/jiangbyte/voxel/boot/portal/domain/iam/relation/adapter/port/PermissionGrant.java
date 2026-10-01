package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限授予明细（含数据范围）。
 *
 * Author: Charlie
 */
public class PermissionGrant {
    private String permissionKey;
    private String dataScope;
    private List<String> customScopeDeptIds = new ArrayList<>();
    private String sourceType;
    private String sourceId;

    public String getPermissionKey() { return permissionKey; }
    public void setPermissionKey(String permissionKey) { this.permissionKey = permissionKey; }
    public String getDataScope() { return dataScope; }
    public void setDataScope(String dataScope) { this.dataScope = dataScope; }
    public List<String> getCustomScopeDeptIds() { return customScopeDeptIds; }
    public void setCustomScopeDeptIds(List<String> customScopeDeptIds) { this.customScopeDeptIds = customScopeDeptIds; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
}

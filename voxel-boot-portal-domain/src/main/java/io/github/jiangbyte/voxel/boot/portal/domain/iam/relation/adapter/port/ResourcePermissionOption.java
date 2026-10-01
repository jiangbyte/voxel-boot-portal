package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

public class ResourcePermissionOption {
    private String id;
    private String permissionKey;
    private String title;
    private String dataScope;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPermissionKey() { return permissionKey; }
    public void setPermissionKey(String permissionKey) { this.permissionKey = permissionKey; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDataScope() { return dataScope; }
    public void setDataScope(String dataScope) { this.dataScope = dataScope; }
}

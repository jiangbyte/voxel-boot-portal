package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.ArrayList;
import java.util.List;

public class ResourceGrantMenuOption {
    private String id;
    private String moduleId;
    private String parentId;
    private String parentIdName;
    private String title;
    private List<ResourcePermissionOption> button = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getParentIdName() { return parentIdName; }
    public void setParentIdName(String parentIdName) { this.parentIdName = parentIdName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<ResourcePermissionOption> getButton() { return button; }
    public void setButton(List<ResourcePermissionOption> button) { this.button = button; }
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

import java.util.ArrayList;
import java.util.List;

public class ResourceGrantModuleOption {
    private String id;
    private String title;
    private List<ResourceGrantMenuOption> menu = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<ResourceGrantMenuOption> getMenu() { return menu; }
    public void setMenu(List<ResourceGrantMenuOption> menu) { this.menu = menu; }
}

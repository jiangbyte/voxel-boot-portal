package io.github.jiangbyte.voxel.boot.portal.cases.iam.relation;

import java.util.ArrayList;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantItem;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.ResourceGrantModuleOption;

/**
 * 主体已拥有资源授权视图。
 *
 * Author: Charlie
 */
public class ResourceOwnView {
    private String id;
    private List<ResourceGrantModuleOption> modules = new ArrayList<>();
    private List<ResourceGrantItem> grantInfoList = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<ResourceGrantModuleOption> getModules() { return modules; }
    public void setModules(List<ResourceGrantModuleOption> modules) { this.modules = modules; }
    public List<ResourceGrantItem> getGrantInfoList() { return grantInfoList; }
    public void setGrantInfoList(List<ResourceGrantItem> grantInfoList) { this.grantInfoList = grantInfoList; }
}

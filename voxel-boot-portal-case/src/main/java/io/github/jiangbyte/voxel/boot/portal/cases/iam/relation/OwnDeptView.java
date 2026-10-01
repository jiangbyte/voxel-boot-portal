package io.github.jiangbyte.voxel.boot.portal.cases.iam.relation;

import java.util.ArrayList;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port.DeptGrantItem;

public class OwnDeptView {
    private String id;
    private List<DeptGrantItem> grantInfoList = new ArrayList<>();

    public OwnDeptView() {}
    public OwnDeptView(String id, List<DeptGrantItem> grantInfoList) {
        this.id = id;
        this.grantInfoList = grantInfoList == null ? new ArrayList<>() : grantInfoList;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<DeptGrantItem> getGrantInfoList() { return grantInfoList; }
    public void setGrantInfoList(List<DeptGrantItem> grantInfoList) { this.grantInfoList = grantInfoList; }
}

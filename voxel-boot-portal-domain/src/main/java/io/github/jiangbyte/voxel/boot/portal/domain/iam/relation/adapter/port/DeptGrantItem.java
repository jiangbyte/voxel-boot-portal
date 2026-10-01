package io.github.jiangbyte.voxel.boot.portal.domain.iam.relation.adapter.port;

/**
 * 账号部门授予明细。
 *
 * Author: Charlie
 */
public class DeptGrantItem {
    private String deptId;
    private Integer isPrimary;

    public DeptGrantItem() {}
    public DeptGrantItem(String deptId, Integer isPrimary) {
        this.deptId = deptId;
        this.isPrimary = isPrimary;
    }

    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }
    public Integer getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Integer isPrimary) { this.isPrimary = isPrimary; }
}

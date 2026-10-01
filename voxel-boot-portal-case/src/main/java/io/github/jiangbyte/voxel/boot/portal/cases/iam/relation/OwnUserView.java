package io.github.jiangbyte.voxel.boot.portal.cases.iam.relation;

import java.util.ArrayList;
import java.util.List;

public class OwnUserView {
    private String id;
    private List<String> accountIds = new ArrayList<>();

    public OwnUserView() {}
    public OwnUserView(String id, List<String> accountIds) {
        this.id = id;
        this.accountIds = accountIds == null ? new ArrayList<>() : accountIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getAccountIds() { return accountIds; }
    public void setAccountIds(List<String> accountIds) { this.accountIds = accountIds; }
}

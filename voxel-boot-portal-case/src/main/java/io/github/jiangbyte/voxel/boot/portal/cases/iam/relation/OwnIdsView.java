package io.github.jiangbyte.voxel.boot.portal.cases.iam.relation;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用 own-* 结果：主体 id + 目标 id 列表。
 *
 * Author: Charlie
 */
public class OwnIdsView {
    private String id;
    private List<String> ids = new ArrayList<>();
    private List<?> details = new ArrayList<>();

    public OwnIdsView() {}
    public OwnIdsView(String id, List<String> ids) {
        this.id = id;
        this.ids = ids == null ? new ArrayList<>() : ids;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getIds() { return ids; }
    public void setIds(List<String> ids) { this.ids = ids; }
    public List<?> getDetails() { return details; }
    public void setDetails(List<?> details) { this.details = details; }
}

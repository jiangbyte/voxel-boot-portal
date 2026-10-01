package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织信息响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "组织信息响应")
public class OrgInfoResult {
    private List<IdNameResult> roleIdNames = new ArrayList<>();
    private List<IdNameResult> deptIdNames = new ArrayList<>();
    private List<IdNameResult> groupIdNames = new ArrayList<>();

    @Data
    public static class IdNameResult {
        private String id;
        private String name;
    }
}

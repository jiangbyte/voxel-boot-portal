package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "我的实名工单分页")
public class RealNameCaseMyPageParam {
    private long current = 1;
    private long size = 10;
    private String businessType;
    private String status;
}

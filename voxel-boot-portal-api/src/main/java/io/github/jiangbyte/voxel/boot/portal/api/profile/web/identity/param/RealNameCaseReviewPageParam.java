package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "实名审核分页")
public class RealNameCaseReviewPageParam {
    private long current = 1;
    private long size = 10;
    private String businessType;
    private String status;
    private String accountId;
}

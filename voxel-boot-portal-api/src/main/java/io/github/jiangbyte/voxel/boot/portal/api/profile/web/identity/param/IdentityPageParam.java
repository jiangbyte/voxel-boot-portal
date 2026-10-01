package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "实名快照分页")
public class IdentityPageParam {
    private long current = 1;
    private long size = 10;
    private String status;
    private String accountId;
    private String documentType;
}

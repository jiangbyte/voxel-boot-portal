package io.github.jiangbyte.voxel.boot.portal.api.iam.web.resource.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资源树查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "资源树查询入参")
@Data
public class SysResourceTreeParam {

    @Schema(description = "模块ID")
    private String moduleId;

    @Schema(description = "模块客户端")
    private String moduleClient;
}

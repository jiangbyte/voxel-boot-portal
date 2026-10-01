package io.github.jiangbyte.voxel.boot.portal.api.auth.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 自助注销请求参数。
 *
 * Author: Charlie
 */
@Schema(description = "自助注销请求参数")
@Data
public class CancelAccountParam {

    @Schema(description = "注销原因")
    private String cancelReason;
}

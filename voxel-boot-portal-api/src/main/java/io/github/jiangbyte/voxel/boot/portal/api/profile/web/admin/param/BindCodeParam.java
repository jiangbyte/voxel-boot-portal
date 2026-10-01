package io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送绑定验证码请求。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "发送绑定验证码请求")
public class BindCodeParam {
    @NotBlank
    @Schema(description = "目标手机或邮箱")
    private String target;
}

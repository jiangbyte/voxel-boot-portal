package io.github.jiangbyte.voxel.boot.portal.api.sys.web.weakpassword.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建弱密码入参。
 *
 * Author: Charlie
 */
@Schema(description = "创建弱密码入参")
@Data
public class SysWeakPasswordAddParam {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "弱口令明文（用于注册/改密校验）")
    private String password;
}

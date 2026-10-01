package io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 按 objectName 查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "按 objectName 查询入参")
@Data
public class SysFileObjectNameParam {

    @NotBlank
    @Schema(description = "对象键")
    private String objectName;
}

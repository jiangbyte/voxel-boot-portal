package io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 编辑文件入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑文件入参")
@Data
public class SysFileEditParam {

    @NotBlank
    @Schema(description = "主键ID")
    private String id;

    @NotBlank
    @Schema(description = "原始文件名")
    private String originalName;
}

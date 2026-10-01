package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 预览响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "预览响应")
public class SysCodegenPreviewResult {
    private List<SysCodegenPreviewFileResult> files;
}

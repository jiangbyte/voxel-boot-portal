package io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预览文件响应。
 *
 * Author: Charlie
 */
@Data
@Schema(description = "预览文件响应")
public class SysCodegenPreviewFileResult {
    private String path;
    private String language;
    private String content;
}

package io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件 URL 响应（占位）。
 *
 * Author: Charlie
 */
@Schema(description = "文件 URL 响应")
@Data
public class SysFileUrlResult {

    @Schema(description = "对象键")
    private String objectName;

    @Schema(description = "可访问 URL")
    private String url;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "大小")
    private Long size;

    @Schema(description = "MIME 类型")
    private String contentType;
}

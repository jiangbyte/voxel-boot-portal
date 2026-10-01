package io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "文件分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysFilePageParam extends PageQuery {

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "存储服务商")
    private String storageProvider;

    @Schema(description = "对象键")
    private String objectName;
}

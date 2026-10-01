package io.github.jiangbyte.voxel.boot.portal.api.sys.web.dict.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "数据字典分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictPageParam extends PageQuery {

    @Schema(description = "字典项编码")
    private String code;

    @Schema(description = "字典分类：SYS / BIZ")
    private String category;

    @Schema(description = "父级字典项ID")
    private String parentId;

    @Schema(description = "字典项状态：ENABLED/DISABLED")
    private String status;
}

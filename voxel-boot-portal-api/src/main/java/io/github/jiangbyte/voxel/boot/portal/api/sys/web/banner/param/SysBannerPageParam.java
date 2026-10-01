package io.github.jiangbyte.voxel.boot.portal.api.sys.web.banner.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Banner 分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "Banner 分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysBannerPageParam extends PageQuery {

    @Schema(description = "可见账户类型过滤")
    private String targetAccountType;

    @Schema(description = "Banner 分类")
    private String category;

    @Schema(description = "Banner 类型")
    private String type;

    @Schema(description = "展示位置")
    private String position;

    @Schema(description = "Banner 状态")
    private String status;
}

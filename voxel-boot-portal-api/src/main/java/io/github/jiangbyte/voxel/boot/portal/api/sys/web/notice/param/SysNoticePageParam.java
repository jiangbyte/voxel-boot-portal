package io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告分页查询入参。
 *
 * Author: Charlie
 */
@Schema(description = "公告分页查询入参")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticePageParam extends PageQuery {

    @Schema(description = "种类")
    private String kind;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "重要等级")
    private String severity;
}

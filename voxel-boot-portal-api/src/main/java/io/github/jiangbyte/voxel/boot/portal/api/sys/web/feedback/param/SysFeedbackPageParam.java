package io.github.jiangbyte.voxel.boot.portal.api.sys.web.feedback.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈分页查询参数。
 *
 * Author: Charlie
 */
@Schema(description = "反馈分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysFeedbackPageParam extends PageQuery {

    @Schema(description = "标题（模糊匹配）")
    private String title;

    @Schema(description = "反馈状态：PENDING/REPLIED/CLOSED 等")
    private String status;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "提交人账户类型：ADMIN / PORTAL")
    private String submitterAccountType;
}

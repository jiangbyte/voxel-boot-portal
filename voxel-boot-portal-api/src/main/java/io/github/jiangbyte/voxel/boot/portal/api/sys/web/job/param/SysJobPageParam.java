package io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.param;

import io.github.jiangbyte.voxel.boot.portal.api.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务分页查询入参。
 *
 * Author: Charlie
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "任务分页查询入参")
public class SysJobPageParam extends PageQuery {
    private String name;
    private String triggerType;
    private Integer enabled;
}

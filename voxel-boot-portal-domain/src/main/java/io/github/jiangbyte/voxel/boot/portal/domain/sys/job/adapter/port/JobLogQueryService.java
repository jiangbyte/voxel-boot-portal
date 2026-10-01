package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

/**
 * 任务日志查询端口。
 *
 * Author: Charlie
 */
public interface JobLogQueryService {

    PageResult<JobLogView> page(JobLogPageQuery query);
}

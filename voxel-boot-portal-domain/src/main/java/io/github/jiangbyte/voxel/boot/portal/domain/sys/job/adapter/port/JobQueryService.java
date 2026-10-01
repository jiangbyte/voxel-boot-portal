package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

/**
 * 任务查询端口。
 *
 * Author: Charlie
 */
public interface JobQueryService {

    JobView detail(String id);

    PageResult<JobView> page(JobPageQuery query);
}

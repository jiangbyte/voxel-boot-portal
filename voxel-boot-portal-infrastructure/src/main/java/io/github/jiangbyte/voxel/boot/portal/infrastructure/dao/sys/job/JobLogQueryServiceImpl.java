package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobLogPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobLogQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobLogView;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 任务日志查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class JobLogQueryServiceImpl implements JobLogQueryService {

    private final JobLogMapper jobLogMapper;


    @Override
    public PageResult<JobLogView> page(JobLogPageQuery query) {
        Page<JobLogPO> page = jobLogMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<JobLogPO>lambdaQuery()
                        .eq(StringUtils.hasText(query.jobId()), JobLogPO::getJobId, query.jobId())
                        .eq(query.success() != null, JobLogPO::getSuccess, query.success())
                        .orderByDesc(JobLogPO::getStartedAt));
        List<JobLogView> records = page.getRecords().stream().map(JobLogAssembler::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }
}

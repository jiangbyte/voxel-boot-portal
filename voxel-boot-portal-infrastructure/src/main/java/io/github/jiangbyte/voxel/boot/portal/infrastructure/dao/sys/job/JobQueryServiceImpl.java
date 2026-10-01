package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;
import lombok.RequiredArgsConstructor;

/**
 * 任务查询实现。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class JobQueryServiceImpl implements JobQueryService {

    private final JobMapper jobMapper;


    @Override
    public JobView detail(String id) {
        JobPO po = jobMapper.selectById(id);
        if (po == null) {
            throw new DomainException(404, "Job not found");
        }
        return JobAssembler.toView(po);
    }

    @Override
    public PageResult<JobView> page(JobPageQuery query) {
        Page<JobPO> page = jobMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<JobPO>lambdaQuery()
                        .like(StringUtils.hasText(query.name()), JobPO::getName, query.name())
                        .eq(StringUtils.hasText(query.triggerType()), JobPO::getTriggerType, query.triggerType())
                        .eq(query.enabled() != null, JobPO::getEnabled, query.enabled())
                        .orderByAsc(JobPO::getSort)
                        .orderByDesc(JobPO::getCreatedAt));
        List<JobView> records = page.getRecords().stream().map(JobAssembler::toView).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }
}

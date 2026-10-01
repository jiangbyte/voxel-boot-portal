package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository.JobRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobPO;
import lombok.RequiredArgsConstructor;

/**
 * 任务仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final JobMapper jobMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public void save(Job job) {
        // 1. 转 PO
        JobPO po = JobAssembler.INSTANCE.toPO(job);
        // 2. 插入或更新
        JobPO existing = jobMapper.selectById(po.getId());
        if (existing == null) {
            jobMapper.insert(po);
        } else {
            jobMapper.updateById(po);
        }
        // 3. 派发事件
        domainEventPublisher.publishAll(job.pullDomainEvents());
    }

    @Override
    public void remove(Job job) {
        if (job == null || !StringUtils.hasText(job.getId())) {
            return;
        }
        jobMapper.deleteById(job.getId());
        domainEventPublisher.publishAll(job.pullDomainEvents());
    }

    @Override
    public Optional<Job> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(JobAssembler.toAggregate(jobMapper.selectById(id)));
    }

    @Override
    public List<Job> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return jobMapper.selectByIds(ids).stream().map(JobAssembler::toAggregate).toList();
    }
}

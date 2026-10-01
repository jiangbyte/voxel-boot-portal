package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.JobLog;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository.JobLogRepository;
import org.springframework.stereotype.Repository;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobLogAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job.JobLogMapper;
import lombok.RequiredArgsConstructor;

/**
 * 任务日志仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class JobLogRepositoryImpl implements JobLogRepository {

    private final JobLogMapper jobLogMapper;


    @Override
    public void save(JobLog log) {
        // 1. 转 PO 并插入
        jobLogMapper.insert(JobLogAssembler.INSTANCE.toPO(log));
    }
}

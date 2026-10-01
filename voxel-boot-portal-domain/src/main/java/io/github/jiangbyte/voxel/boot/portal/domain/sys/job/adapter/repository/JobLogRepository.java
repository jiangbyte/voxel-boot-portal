package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.JobLog;

/**
 * 任务执行日志仓储端口。
 *
 * Author: Charlie
 */
public interface JobLogRepository {

    void save(JobLog log);
}

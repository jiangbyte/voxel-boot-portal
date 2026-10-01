package io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository;

import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;

/**
 * 任务仓储端口。
 *
 * Author: Charlie
 */
public interface JobRepository {

    void save(Job job);

    void remove(Job job);

    Optional<Job> findById(String id);

    List<Job> findByIds(List<String> ids);
}

package io.github.jiangbyte.voxel.boot.portal.cases.sys.job;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.repository.JobRepository;
import java.time.LocalDateTime;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobRunner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobTriggerCalculator;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IJobCase {
    void create(CreateJobCommand command);
    void update(UpdateJobCommand command);
    void delete(List<String> ids);
    void updateEnabled(String id, Integer enabled);
    void runNow(String id, String executorAccountId);
}

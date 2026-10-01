package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.JobLog;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobLogView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 任务日志转换。
 */
@Mapper
public interface JobLogAssembler {

    JobLogAssembler INSTANCE = Mappers.getMapper(JobLogAssembler.class);

    JobLogPO toPO(JobLog log);


    public static JobLogView toView(JobLogPO po) {
        if (po == null) {
            return null;
        }
        return new JobLogView(
                po.getId(), po.getJobId(), po.getParams(), po.getStartedAt(), po.getDurationMs(),
                po.getSuccess(), po.getResult(), po.getExecutor(), po.getIp(), po.getProcessId(),
                po.getAppDir(), po.getCreatedAt());
    }
}

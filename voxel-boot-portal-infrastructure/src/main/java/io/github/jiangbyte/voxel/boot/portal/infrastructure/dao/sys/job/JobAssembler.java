package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.job;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.Job;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 任务聚合/视图与 PO 转换。
 */
@Mapper
public interface JobAssembler {

    JobAssembler INSTANCE = Mappers.getMapper(JobAssembler.class);

    JobPO toPO(Job job);


    public static Job toAggregate(JobPO po) {
        if (po == null) {
            return null;
        }
        return Job.rehydrate(
                po.getId(), po.getName(), po.getHandler(), po.getTriggerType(), po.getTriggerConfig(),
                po.getParams(), po.getLastRunTime(), po.getNextRunTime(), po.getLastResult(),
                po.getEnabled(), po.getDescription(), po.getSort());
    }

    public static JobView toView(JobPO po) {
        if (po == null) {
            return null;
        }
        return new JobView(
                po.getId(), po.getName(), po.getHandler(), po.getTriggerType(), po.getTriggerConfig(),
                po.getParams(), po.getLastRunTime(), po.getNextRunTime(), po.getLastResult(),
                po.getEnabled(), po.getDescription(), po.getSort(), po.getCreatedAt(), po.getUpdatedAt());
    }
}

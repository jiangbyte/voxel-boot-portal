package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.job.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.result.SysJobLogResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobLogView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 JobLogView 装配为 API 响应 SysJobLogResult。
 */
@Mapper
public interface SysJobLogResultAssembler {

    SysJobLogResultAssembler INSTANCE = Mappers.getMapper(SysJobLogResultAssembler.class);

    SysJobLogResult toResult(JobLogView view);
}

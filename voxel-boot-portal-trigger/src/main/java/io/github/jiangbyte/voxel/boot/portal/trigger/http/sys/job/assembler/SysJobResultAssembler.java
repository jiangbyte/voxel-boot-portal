package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.job.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.job.result.SysJobResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.job.adapter.port.JobView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 JobView 装配为 API 响应 SysJobResult。
 */
@Mapper
public interface SysJobResultAssembler {

    SysJobResultAssembler INSTANCE = Mappers.getMapper(SysJobResultAssembler.class);

    SysJobResult toResult(JobView view);
}

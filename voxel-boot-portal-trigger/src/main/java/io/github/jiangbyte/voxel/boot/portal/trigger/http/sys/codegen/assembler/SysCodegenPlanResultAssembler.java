package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.codegen.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result.SysCodegenPlanResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPlanView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 CodegenPlanView 装配为 API 响应 SysCodegenPlanResult。
 */
@Mapper
public interface SysCodegenPlanResultAssembler {

    SysCodegenPlanResultAssembler INSTANCE = Mappers.getMapper(SysCodegenPlanResultAssembler.class);

    SysCodegenPlanResult toResult(CodegenPlanView view);
}

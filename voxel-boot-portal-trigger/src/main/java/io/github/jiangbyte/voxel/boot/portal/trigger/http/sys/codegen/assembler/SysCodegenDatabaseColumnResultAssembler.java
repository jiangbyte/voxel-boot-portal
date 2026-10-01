package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.codegen.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result.SysCodegenDatabaseColumnResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenDatabaseColumnView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 CodegenDatabaseColumnView 装配为 API 响应 SysCodegenDatabaseColumnResult。
 */
@Mapper
public interface SysCodegenDatabaseColumnResultAssembler {

    SysCodegenDatabaseColumnResultAssembler INSTANCE = Mappers.getMapper(SysCodegenDatabaseColumnResultAssembler.class);

    SysCodegenDatabaseColumnResult toResult(CodegenDatabaseColumnView view);
}

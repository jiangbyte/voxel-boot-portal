package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.codegen.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result.SysCodegenDatabaseTableResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenDatabaseTableView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 CodegenDatabaseTableView 装配为 API 响应 SysCodegenDatabaseTableResult。
 */
@Mapper
public interface SysCodegenDatabaseTableResultAssembler {

    SysCodegenDatabaseTableResultAssembler INSTANCE = Mappers.getMapper(SysCodegenDatabaseTableResultAssembler.class);

    SysCodegenDatabaseTableResult toResult(CodegenDatabaseTableView view);
}

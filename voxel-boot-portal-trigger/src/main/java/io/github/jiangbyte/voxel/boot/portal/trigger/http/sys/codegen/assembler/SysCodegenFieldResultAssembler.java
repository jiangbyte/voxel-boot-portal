package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.codegen.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result.SysCodegenFieldResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenFieldView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 CodegenFieldView 装配为 API 响应 SysCodegenFieldResult。
 */
@Mapper
public interface SysCodegenFieldResultAssembler {

    SysCodegenFieldResultAssembler INSTANCE = Mappers.getMapper(SysCodegenFieldResultAssembler.class);

    SysCodegenFieldResult toResult(CodegenFieldView view);
}

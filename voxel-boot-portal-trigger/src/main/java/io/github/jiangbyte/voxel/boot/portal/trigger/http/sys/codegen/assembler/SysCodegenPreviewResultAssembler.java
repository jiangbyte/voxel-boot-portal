package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.codegen.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result.SysCodegenPreviewResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPreviewView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 CodegenPreviewView 装配为 API 响应 SysCodegenPreviewResult。
 */
@Mapper(uses = SysCodegenPreviewFileResultAssembler.class)
public interface SysCodegenPreviewResultAssembler {

    SysCodegenPreviewResultAssembler INSTANCE = Mappers.getMapper(SysCodegenPreviewResultAssembler.class);

    SysCodegenPreviewResult toResult(CodegenPreviewView view);
}

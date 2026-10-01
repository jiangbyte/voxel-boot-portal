package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.codegen.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.codegen.result.SysCodegenPreviewFileResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.codegen.adapter.port.CodegenPreviewFileView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 CodegenPreviewFileView 装配为 API 响应 SysCodegenPreviewFileResult。
 */
@Mapper
public interface SysCodegenPreviewFileResultAssembler {

    SysCodegenPreviewFileResultAssembler INSTANCE = Mappers.getMapper(SysCodegenPreviewFileResultAssembler.class);

    SysCodegenPreviewFileResult toResult(CodegenPreviewFileView view);
}

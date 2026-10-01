package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.file.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.result.SysFileResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 FileView 装配为 API 响应 SysFileResult。
 */
@Mapper
public interface SysFileResultAssembler {

    SysFileResultAssembler INSTANCE = Mappers.getMapper(SysFileResultAssembler.class);

    SysFileResult toResult(FileView view);
}

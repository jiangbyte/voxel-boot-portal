package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.config.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.config.result.SysConfigResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 ConfigView 装配为 API 响应 SysConfigResult。
 */
@Mapper
public interface SysConfigResultAssembler {

    SysConfigResultAssembler INSTANCE = Mappers.getMapper(SysConfigResultAssembler.class);

    @Mapping(source = "builtin", target = "isBuiltin")
    SysConfigResult toResult(ConfigView view);
}

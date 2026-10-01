package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.position.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.position.result.SysPositionResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.adapter.port.PositionView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 PositionView 装配为 API 响应 SysPositionResult。
 */
@Mapper
public interface SysPositionResultAssembler {

    SysPositionResultAssembler INSTANCE = Mappers.getMapper(SysPositionResultAssembler.class);

    @Mapping(source = "virtual", target = "isVirtual")
    SysPositionResult toResult(PositionView view);
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.group.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.result.SysGroupResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.adapter.port.GroupView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 GroupView 装配为 API 响应 SysGroupResult。
 */
@Mapper
public interface SysGroupResultAssembler {

    SysGroupResultAssembler INSTANCE = Mappers.getMapper(SysGroupResultAssembler.class);

    SysGroupResult toResult(GroupView view);
}

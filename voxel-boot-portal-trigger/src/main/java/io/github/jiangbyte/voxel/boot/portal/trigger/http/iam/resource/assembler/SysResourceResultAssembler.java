package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.resource.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.resource.result.SysResourceResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 ResourceView 装配为 API 响应 SysResourceResult。
 */
@Mapper
public interface SysResourceResultAssembler {

    SysResourceResultAssembler INSTANCE = Mappers.getMapper(SysResourceResultAssembler.class);

    @Mapping(source = "visible", target = "isVisible")
    @Mapping(source = "cache", target = "isCache")
    @Mapping(source = "affix", target = "isAffix")
    SysResourceResult toResult(ResourceView view);
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.dept.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.dept.result.SysDeptResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.adapter.port.DeptView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 DeptView 装配为 API 响应 SysDeptResult。
 */
@Mapper
public interface SysDeptResultAssembler {

    SysDeptResultAssembler INSTANCE = Mappers.getMapper(SysDeptResultAssembler.class);

    @Mapping(source = "virtual", target = "isVirtual")
    SysDeptResult toResult(DeptView view);
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.role.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.role.result.SysRoleResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.adapter.port.RoleView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 RoleView 装配为 API 响应 SysRoleResult。
 */
@Mapper
public interface SysRoleResultAssembler {

    SysRoleResultAssembler INSTANCE = Mappers.getMapper(SysRoleResultAssembler.class);

    @Mapping(source = "builtin", target = "isBuiltin")
    SysRoleResult toResult(RoleView view);
}

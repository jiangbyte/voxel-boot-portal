package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.org.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.org.result.SysOrgResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.adapter.port.OrgView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 OrgView 装配为 API 响应 SysOrgResult。
 */
@Mapper
public interface SysOrgResultAssembler {

    SysOrgResultAssembler INSTANCE = Mappers.getMapper(SysOrgResultAssembler.class);

    @Mapping(source = "virtual", target = "isVirtual")
    SysOrgResult toResult(OrgView view);
}

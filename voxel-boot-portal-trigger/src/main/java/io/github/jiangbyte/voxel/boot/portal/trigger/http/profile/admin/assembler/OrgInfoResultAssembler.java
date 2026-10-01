package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.admin.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result.OrgInfoResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.admin.OrgInfoView;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 OrgInfoView 装配为 API 响应 OrgInfoResult。
 */
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface OrgInfoResultAssembler {

    OrgInfoResultAssembler INSTANCE = Mappers.getMapper(OrgInfoResultAssembler.class);

    OrgInfoResult toResult(OrgInfoView view);

    OrgInfoResult.IdNameResult toIdName(OrgInfoView.IdNameView view);
}

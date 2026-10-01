package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.MeResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.PortalMeView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 PortalMeView 装配为 API 响应 MeResult。
 */
@Mapper(uses = UserProfileResultAssembler.class)
public interface MeResultAssembler {

    MeResultAssembler INSTANCE = Mappers.getMapper(MeResultAssembler.class);

    MeResult toResult(PortalMeView view);
}

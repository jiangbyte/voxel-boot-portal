package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.UserProfileResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.PortalProfileView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 PortalProfileView 装配为 API 响应 UserProfileResult。
 */
@Mapper
public interface UserProfileResultAssembler {

    UserProfileResultAssembler INSTANCE = Mappers.getMapper(UserProfileResultAssembler.class);

    UserProfileResult toResult(PortalProfileView view);
}

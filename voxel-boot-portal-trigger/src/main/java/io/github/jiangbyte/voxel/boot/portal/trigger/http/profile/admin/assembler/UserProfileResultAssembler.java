package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.admin.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result.UserProfileResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.admin.AdminProfileView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 AdminProfileView 装配为 API 响应 UserProfileResult。
 */
@Mapper
public interface UserProfileResultAssembler {

    UserProfileResultAssembler INSTANCE = Mappers.getMapper(UserProfileResultAssembler.class);

    UserProfileResult toResult(AdminProfileView view);
}

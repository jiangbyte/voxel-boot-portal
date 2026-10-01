package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.admin.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.admin.result.MeResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.admin.AdminMeView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 AdminMeView 装配为 API 响应 MeResult。
 */
@Mapper(uses = UserProfileResultAssembler.class)
public interface MeResultAssembler {

    MeResultAssembler INSTANCE = Mappers.getMapper(MeResultAssembler.class);

    MeResult toResult(AdminMeView view);
}

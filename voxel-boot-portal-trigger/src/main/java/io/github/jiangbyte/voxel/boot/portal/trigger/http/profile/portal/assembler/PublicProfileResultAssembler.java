package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.PublicProfileResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.PublicProfileView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 PublicProfileView 装配为 API 响应 PublicProfileResult。
 */
@Mapper
public interface PublicProfileResultAssembler {

    PublicProfileResultAssembler INSTANCE = Mappers.getMapper(PublicProfileResultAssembler.class);

    PublicProfileResult toResult(PublicProfileView view);
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.portal.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.portal.result.AvatarUpdateResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.AvatarUpdateView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 AvatarUpdateView 装配为 API 响应 AvatarUpdateResult。
 */
@Mapper
public interface AvatarUpdateResultAssembler {

    AvatarUpdateResultAssembler INSTANCE = Mappers.getMapper(AvatarUpdateResultAssembler.class);

    AvatarUpdateResult toResult(AvatarUpdateView view);
}

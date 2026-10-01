package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.IdentityPageResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.IdentityPageView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 IdentityPageView 装配为 API 响应 IdentityPageResult。
 */
@Mapper
public interface IdentityPageResultAssembler {

    IdentityPageResultAssembler INSTANCE = Mappers.getMapper(IdentityPageResultAssembler.class);

    IdentityPageResult toResult(IdentityPageView view);
}

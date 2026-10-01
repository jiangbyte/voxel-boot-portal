package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.IdentityStatusResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.IdentityStatusView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 IdentityStatusView 装配为 API 响应 IdentityStatusResult。
 */
@Mapper(uses = RealNameCaseSummaryResultAssembler.class)
public interface IdentityStatusResultAssembler {

    IdentityStatusResultAssembler INSTANCE = Mappers.getMapper(IdentityStatusResultAssembler.class);

    IdentityStatusResult toResult(IdentityStatusView view);
}

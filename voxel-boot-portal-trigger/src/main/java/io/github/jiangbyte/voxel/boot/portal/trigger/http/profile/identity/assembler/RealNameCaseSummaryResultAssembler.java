package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseSummaryResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.RealNameCaseSummaryView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 RealNameCaseSummaryView 装配为 API 响应 RealNameCaseSummaryResult。
 */
@Mapper
public interface RealNameCaseSummaryResultAssembler {

    RealNameCaseSummaryResultAssembler INSTANCE = Mappers.getMapper(RealNameCaseSummaryResultAssembler.class);

    RealNameCaseSummaryResult toResult(RealNameCaseSummaryView view);
}

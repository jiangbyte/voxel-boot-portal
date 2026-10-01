package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseDetailResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.RealNameCaseDetailView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 RealNameCaseDetailView 装配为 API 响应 RealNameCaseDetailResult。
 */
@Mapper(uses = RealNameCaseAttachmentResultAssembler.class)
public interface RealNameCaseDetailResultAssembler {

    RealNameCaseDetailResultAssembler INSTANCE = Mappers.getMapper(RealNameCaseDetailResultAssembler.class);

    RealNameCaseDetailResult toResult(RealNameCaseDetailView view);
}

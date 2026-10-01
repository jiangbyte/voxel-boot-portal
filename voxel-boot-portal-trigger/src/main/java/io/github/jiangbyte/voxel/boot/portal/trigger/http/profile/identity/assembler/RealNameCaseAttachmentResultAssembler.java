package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseAttachmentResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.RealNameCaseAttachmentView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 RealNameCaseAttachmentView 装配为 API 响应 RealNameCaseAttachmentResult。
 */
@Mapper
public interface RealNameCaseAttachmentResultAssembler {

    RealNameCaseAttachmentResultAssembler INSTANCE = Mappers.getMapper(RealNameCaseAttachmentResultAssembler.class);

    RealNameCaseAttachmentResult toResult(RealNameCaseAttachmentView view);
}

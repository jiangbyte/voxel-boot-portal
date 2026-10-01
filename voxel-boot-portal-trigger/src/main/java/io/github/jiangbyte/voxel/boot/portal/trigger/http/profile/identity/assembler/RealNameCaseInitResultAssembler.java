package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseInitResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.RealNameCaseInitView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 RealNameCaseInitView 装配为 API 响应 RealNameCaseInitResult。
 */
@Mapper
public interface RealNameCaseInitResultAssembler {

    RealNameCaseInitResultAssembler INSTANCE = Mappers.getMapper(RealNameCaseInitResultAssembler.class);

    RealNameCaseInitResult toResult(RealNameCaseInitView view);
}

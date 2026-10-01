package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseOptionsResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.RealNameCaseOptionsView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.ArrayList;
import java.util.List;

/**
 * 将应用层 RealNameCaseOptionsView 装配为 API 响应 RealNameCaseOptionsResult。
 */
@Mapper
public interface RealNameCaseOptionsResultAssembler {

    RealNameCaseOptionsResultAssembler INSTANCE = Mappers.getMapper(RealNameCaseOptionsResultAssembler.class);

    RealNameCaseOptionsResult toResult(RealNameCaseOptionsView view);
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.feedback.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.feedback.result.SysFeedbackResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 FeedbackView 装配为 API 响应 SysFeedbackResult。
 */
@Mapper
public interface SysFeedbackResultAssembler {

    SysFeedbackResultAssembler INSTANCE = Mappers.getMapper(SysFeedbackResultAssembler.class);

    SysFeedbackResult toResult(FeedbackView view);
}

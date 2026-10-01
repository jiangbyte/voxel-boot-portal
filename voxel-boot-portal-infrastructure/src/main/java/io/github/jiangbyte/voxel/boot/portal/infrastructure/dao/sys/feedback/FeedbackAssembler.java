package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.feedback;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.Feedback;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.FeedbackStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 反馈聚合与持久化对象互转。
 */
@Mapper
public interface FeedbackAssembler {

    FeedbackAssembler INSTANCE = Mappers.getMapper(FeedbackAssembler.class);

    @Mapping(target = "status", expression = "java(feedback.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.FeedbackStatus.PENDING.name() : feedback.getStatus().name())")
    FeedbackPO toPO(Feedback feedback);


    /**
     * 持久化对象转聚合。
     */
    public static Feedback toAggregate(FeedbackPO po) {
        if (po == null) {
            return null;
        }
        return Feedback.reconstitute(
                po.getId(),
                po.getTitle(),
                po.getContent(),
                po.getCategory(),
                po.getContact(),
                po.getAttachObjectNames(),
                po.getStatus(),
                po.getReply(),
                po.getRepliedBy(),
                po.getRepliedAt(),
                po.getSubmitterAccountType(),
                po.getSubmitterAccountId());
    }
}

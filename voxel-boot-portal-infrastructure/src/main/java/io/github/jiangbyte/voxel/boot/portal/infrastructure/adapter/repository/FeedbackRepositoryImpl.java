package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.Feedback;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.repository.FeedbackRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.feedback.FeedbackAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.feedback.FeedbackMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.feedback.FeedbackPO;
import lombok.RequiredArgsConstructor;

/**
 * 反馈聚合仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final FeedbackMapper feedbackMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<Feedback> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(FeedbackAssembler.toAggregate(feedbackMapper.selectById(id)));
    }

    @Override
    public void save(Feedback aggregate) {
        // 1. 聚合转 PO
        FeedbackPO po = FeedbackAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        FeedbackPO existing = feedbackMapper.selectById(po.getId());
        if (existing == null) {
            feedbackMapper.insert(po);
        } else {
            feedbackMapper.updateById(po);
        }
        // 3. 取出并派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        feedbackMapper.deleteById(id);
    }

    @Override
    public void remove(Feedback aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        feedbackMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

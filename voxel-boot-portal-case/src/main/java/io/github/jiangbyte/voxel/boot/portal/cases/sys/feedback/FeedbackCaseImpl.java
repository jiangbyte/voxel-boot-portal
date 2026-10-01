package io.github.jiangbyte.voxel.boot.portal.cases.sys.feedback;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.Feedback;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 反馈写用例：提交、更新回复、删除。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class FeedbackCaseImpl implements IFeedbackCase {

    private final FeedbackRepository feedbackRepository;


    /**
     * 提交反馈。
     */
    @Transactional
    public void submit(SubmitFeedbackCommand command) {
        // 1. 工厂创建聚合（默认 PENDING，标题/分类补默认值）
        Feedback feedback = Feedback.submit(
                command.title(),
                command.content(),
                command.category(),
                command.contact(),
                toJsonArray(command.attachObjectNames()),
                command.submitterAccountType(),
                command.submitterAccountId());
        // 2. 持久化
        feedbackRepository.save(feedback);
    }

    /**
     * 管理端更新状态或回复。
     */
    @Transactional
    public void update(UpdateFeedbackCommand command) {
        // 1. 按主键加载，不存在则 404
        Feedback feedback = feedbackRepository.findById(command.id())
                .orElseThrow(() -> new DomainException(404, "反馈不存在"));
        // 2. 聚合内更新状态/回复
        feedback.updateReply(command.status(), command.reply(), command.repliedBy());
        // 3. 保存
        feedbackRepository.save(feedback);
    }

    /**
     * 批量删除反馈。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个加载、登记删除事件后移除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            feedbackRepository.findById(id).ifPresent(feedback -> {
                feedback.markDeleted();
                feedbackRepository.remove(feedback);
            });
        }
    }

    private static String toJsonArray(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String raw : values) {
            String value = raw == null ? "" : raw.trim();
            if (value.isEmpty()) {
                continue;
            }
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"')
                    .append(value.replace("\\", "\\\\").replace("\"", "\\\""))
                    .append('"');
        }
        if (first) {
            return "[]";
        }
        sb.append(']');
        return sb.toString();
    }
}

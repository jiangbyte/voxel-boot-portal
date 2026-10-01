package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 反馈只读视图。
 *
 * Author: Charlie
 */
public record FeedbackView(
        String id,
        String title,
        String content,
        String category,
        String contact,
        List<String> attachObjectNames,
        String status,
        String reply,
        String repliedBy,
        LocalDateTime repliedAt,
        String submitterAccountType,
        String submitterAccountId,
        String submitterAvatar,
        String submitterNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

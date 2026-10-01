package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port;

/**
 * 反馈分页查询条件。
 *
 * Author: Charlie
 */
public record FeedbackPageQuery(
        long current,
        long size,
        String title,
        String status,
        String category,
        String submitterAccountType
) {
}

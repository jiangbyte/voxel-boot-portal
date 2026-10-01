package io.github.jiangbyte.voxel.boot.portal.cases.sys.feedback;

/**
 * 管理端更新反馈命令（状态与回复）。
 *
 * Author: Charlie
 */
public record UpdateFeedbackCommand(
        String id,
        String status,
        String reply,
        String repliedBy
) {
}

package io.github.jiangbyte.voxel.boot.portal.cases.sys.feedback;

import java.util.List;

/**
 * 提交反馈命令。
 *
 * Author: Charlie
 */
public record SubmitFeedbackCommand(
        String title,
        String content,
        String category,
        String contact,
        List<String> attachObjectNames,
        String submitterAccountType,
        String submitterAccountId
) {
}

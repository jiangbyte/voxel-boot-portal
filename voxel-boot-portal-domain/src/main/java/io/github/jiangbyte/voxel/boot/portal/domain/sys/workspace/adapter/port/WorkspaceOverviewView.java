package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port;

import java.util.List;

/**
 * 工作台总览视图：快捷应用 + 近期操作/登录。
 *
 * Author: Charlie
 */
public record WorkspaceOverviewView(
        List<WorkspaceShortcutView> shortcuts,
        List<WorkspaceActivityItemView> recentOperations,
        List<WorkspaceActivityItemView> recentLogins
) {
}

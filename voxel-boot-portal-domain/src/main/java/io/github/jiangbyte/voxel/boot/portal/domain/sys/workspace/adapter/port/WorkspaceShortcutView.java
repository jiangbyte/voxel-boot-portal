package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port;

/**
 * 工作台个人快捷应用视图。
 *
 * Author: Charlie
 */
public record WorkspaceShortcutView(
        String id,
        String resourceId,
        Integer sort,
        String name,
        String path,
        String icon,
        String code
) {
}

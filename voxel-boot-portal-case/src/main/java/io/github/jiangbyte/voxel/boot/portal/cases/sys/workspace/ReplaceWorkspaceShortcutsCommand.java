package io.github.jiangbyte.voxel.boot.portal.cases.sys.workspace;

import java.util.List;

/**
 * 全量替换当前账号快捷应用。
 *
 * Author: Charlie
 */
public record ReplaceWorkspaceShortcutsCommand(List<String> resourceIds) {
}

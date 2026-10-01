package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.repository;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.WorkspaceShortcut;

/**
 * 工作台快捷应用仓储。
 *
 * Author: Charlie
 */
public interface WorkspaceShortcutRepository {

    /**
     * 全量替换某账号的快捷应用（先删后插）。
     */
    void replaceAll(String accountId, List<WorkspaceShortcut> shortcuts);

    /**
     * 按账号查询，按 sort、id 升序。
     */
    List<WorkspaceShortcut> listByAccountId(String accountId);
}

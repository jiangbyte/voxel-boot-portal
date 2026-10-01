package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port;

import java.util.List;

/**
 * 工作台快捷应用查询端口。
 *
 * Author: Charlie
 */
public interface WorkspaceShortcutQueryService {

    /**
     * 当前账号可用快捷应用列表。
     */
    List<WorkspaceShortcutView> listMine();
}

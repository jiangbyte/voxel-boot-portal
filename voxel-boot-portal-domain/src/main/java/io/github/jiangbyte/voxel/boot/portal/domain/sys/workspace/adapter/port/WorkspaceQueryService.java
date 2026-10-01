package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port;

/**
 * 工作台只读查询端口。
 *
 * Author: Charlie
 */
public interface WorkspaceQueryService {

    /**
     * 当前管理员工作台总览。
     */
    WorkspaceOverviewView overview();
}

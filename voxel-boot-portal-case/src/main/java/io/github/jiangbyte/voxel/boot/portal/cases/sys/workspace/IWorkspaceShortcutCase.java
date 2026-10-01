package io.github.jiangbyte.voxel.boot.portal.cases.sys.workspace;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.WorkspaceShortcut;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.repository.WorkspaceShortcutRepository;
import java.util.ArrayList;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutView;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IWorkspaceShortcutCase {
    List<WorkspaceShortcutView> replaceMine(ReplaceWorkspaceShortcutsCommand command);
}

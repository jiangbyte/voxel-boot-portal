package io.github.jiangbyte.voxel.boot.portal.cases.sys.workspace;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.WorkspaceShortcut;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.repository.WorkspaceShortcutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutView;
import lombok.RequiredArgsConstructor;

/**
 * 工作台快捷应用写用例：全量替换本人快捷菜单。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class WorkspaceShortcutCaseImpl implements IWorkspaceShortcutCase {

    public static final int MAX_SHORTCUTS = 16;
    public static final String HOME_CODE = "sys-workspace";

    private final SessionPort sessionPort;
    private final WorkspaceShortcutRepository shortcutRepository;
    private final ResourceQueryService resourceQueryService;
    private final WorkspaceShortcutQueryService shortcutQueryService;


    /**
     * 全量替换当前账号快捷应用，返回最新列表。
     */
    @Transactional
    public List<WorkspaceShortcutView> replaceMine(ReplaceWorkspaceShortcutsCommand command) {
        // 1. 解析当前管理员账号
        String accountId = requireAdminAccountId();
        // 2. 规范化资源 ID（去重、上限）
        List<String> normalized = WorkspaceShortcut.normalizeResourceIds(
                command == null ? null : command.resourceIds(), MAX_SHORTCUTS);
        // 3. 逐个校验 MENU+ENABLED+有 path，且排除工作台首页
        // IAM 授权集合尚未就绪：暂不过滤 grants，只要资源为已启用 MENU 且存在即可保存
        List<WorkspaceShortcut> entities = new ArrayList<>(normalized.size());
        int sort = 1;
        for (String resourceId : normalized) {
            ResourceView menu;
            try {
                menu = resourceQueryService.detail(resourceId);
            } catch (DomainException ex) {
                throw new DomainException("存在不可用的菜单资源");
            }
            if (menu == null
                    || !"MENU".equals(menu.resourceType())
                    || !"ENABLED".equals(menu.status())
                    || !StringUtils.hasText(menu.path())
                    || HOME_CODE.equals(menu.code())) {
                throw new DomainException("存在不可用的菜单资源");
            }
            entities.add(WorkspaceShortcut.create(accountId, resourceId, sort++));
        }
        // 4. 先删后插（仓储内派发替换事件）
        shortcutRepository.replaceAll(accountId, entities);
        // 5. 返回最新可用列表
        return shortcutQueryService.listMine();
    }

    private String requireAdminAccountId() {
        String accountId = sessionPort.currentAdminAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        return accountId;
    }
}

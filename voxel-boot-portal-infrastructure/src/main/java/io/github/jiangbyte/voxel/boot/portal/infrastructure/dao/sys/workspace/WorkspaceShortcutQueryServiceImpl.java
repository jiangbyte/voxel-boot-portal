package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.WorkspaceShortcut;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.repository.WorkspaceShortcutRepository;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourceMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.resource.ResourcePO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/**
 * 工作台快捷应用只读查询：关联启用 MENU 并过滤无 path 项。
 * <p>IAM 授权集合尚未就绪：列表展示全部已启用且存在的 MENU（不做 grant 过滤）。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class WorkspaceShortcutQueryServiceImpl implements WorkspaceShortcutQueryService {

    private final SessionPort sessionPort;
    private final WorkspaceShortcutRepository shortcutRepository;
    private final ResourceMapper resourceMapper;


    @Override
    public List<WorkspaceShortcutView> listMine() {
        // 1. 当前管理员账号
        String accountId = sessionPort.currentAdminAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "未登录");
        }
        // 2. 加载本人快捷项
        List<WorkspaceShortcut> shortcuts = shortcutRepository.listByAccountId(accountId);
        if (shortcuts.isEmpty()) {
            return List.of();
        }
        // 3. 批量加载启用 MENU 资源
        List<String> resourceIds = shortcuts.stream()
                .map(WorkspaceShortcut::getResourceId)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
        Map<String, ResourcePO> menuMap = resourceIds.isEmpty()
                ? Map.of()
                : resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                                .in(ResourcePO::getId, resourceIds)
                                .eq(ResourcePO::getStatus, "ENABLED")
                                .eq(ResourcePO::getResourceType, "MENU"))
                        .stream()
                        .collect(Collectors.toMap(ResourcePO::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        // 4. 组装视图；IAM grants 未就绪前展示全部 ENABLED MENU（见类注释）
        List<WorkspaceShortcutView> rows = new ArrayList<>();
        for (WorkspaceShortcut shortcut : shortcuts) {
            ResourcePO menu = menuMap.get(shortcut.getResourceId());
            if (menu == null || !StringUtils.hasText(menu.getPath())) {
                continue;
            }
            rows.add(new WorkspaceShortcutView(
                    shortcut.getId(),
                    shortcut.getResourceId(),
                    shortcut.getSort(),
                    menu.getName(),
                    menu.getPath(),
                    menu.getIcon(),
                    menu.getCode()));
        }
        return rows;
    }
}

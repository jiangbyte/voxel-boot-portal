package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.event.WorkspaceShortcutsReplaced;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * 工作台快捷应用聚合：单条快捷项；替换场景由应用层批量组装后落库。
 *
 * Author: Charlie
 */
public class WorkspaceShortcut extends AggregateRoot<String> {

    private String id;
    private String accountId;
    private String resourceId;
    private Integer sort;

    private WorkspaceShortcut() {
    }

    /**
     * 新建快捷项。
     */
    public static WorkspaceShortcut create(String accountId, String resourceId, int sort) {
        // 1. 校验账号与资源 ID
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException("accountId must not be blank");
        }
        if (resourceId == null || resourceId.isBlank()) {
            throw new DomainException("resourceId must not be blank");
        }
        // 2. 组装聚合
        WorkspaceShortcut shortcut = new WorkspaceShortcut();
        shortcut.id = UUID.randomUUID().toString().replace("-", "");
        shortcut.accountId = accountId.trim();
        shortcut.resourceId = resourceId.trim();
        shortcut.sort = sort;
        return shortcut;
    }

    /**
     * 从持久化重建（不触发事件）。
     */
    public static WorkspaceShortcut reconstitute(String id, String accountId, String resourceId, Integer sort) {
        WorkspaceShortcut shortcut = new WorkspaceShortcut();
        shortcut.id = id;
        shortcut.accountId = accountId;
        shortcut.resourceId = resourceId;
        shortcut.sort = sort == null ? 0 : sort;
        return shortcut;
    }

    /**
     * 规范化资源 ID 列表：去空白、保序去重；超限抛错。
     */
    public static List<String> normalizeResourceIds(List<String> resourceIds, int maxSize) {
        // 1. 保序去重
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        if (resourceIds != null) {
            for (String id : resourceIds) {
                if (id != null && !id.isBlank()) {
                    unique.add(id.trim());
                }
            }
        }
        // 2. 上限校验
        if (unique.size() > maxSize) {
            throw new DomainException("快捷应用最多 " + maxSize + " 个");
        }
        return new ArrayList<>(unique);
    }

    /**
     * 登记全量替换事件（由应用层在替换成功后调用一次）。
     */
    public static WorkspaceShortcut markReplacedEvent(String accountId, List<String> resourceIds) {
        WorkspaceShortcut holder = new WorkspaceShortcut();
        holder.id = accountId;
        holder.accountId = accountId;
        holder.registerEvent(new WorkspaceShortcutsReplaced(accountId, List.copyOf(resourceIds)));
        return holder;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Integer getSort() {
        return sort;
    }
}

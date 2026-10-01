package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.WorkspaceShortcut;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.repository.WorkspaceShortcutRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace.WorkspaceShortcutAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace.WorkspaceShortcutMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace.WorkspaceShortcutPO;
import lombok.RequiredArgsConstructor;

/**
 * 工作台快捷应用仓储实现。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class WorkspaceShortcutRepositoryImpl implements WorkspaceShortcutRepository {

    private final WorkspaceShortcutMapper shortcutMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public void replaceAll(String accountId, List<WorkspaceShortcut> shortcuts) {
        // 1. 删除该账号全部快捷项
        shortcutMapper.delete(Wrappers.<WorkspaceShortcutPO>lambdaQuery()
                .eq(WorkspaceShortcutPO::getAccountId, accountId));
        // 2. 按序插入新项
        if (shortcuts != null) {
            for (WorkspaceShortcut shortcut : shortcuts) {
                shortcutMapper.insert(WorkspaceShortcutAssembler.INSTANCE.toPO(shortcut));
            }
        }
        // 3. 派发替换事件
        List<String> resourceIds = shortcuts == null
                ? List.of()
                : shortcuts.stream().map(WorkspaceShortcut::getResourceId).toList();
        WorkspaceShortcut eventHolder = WorkspaceShortcut.markReplacedEvent(accountId, resourceIds);
        domainEventPublisher.publishAll(eventHolder.pullDomainEvents());
    }

    @Override
    public List<WorkspaceShortcut> listByAccountId(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return List.of();
        }
        return shortcutMapper.selectList(Wrappers.<WorkspaceShortcutPO>lambdaQuery()
                        .eq(WorkspaceShortcutPO::getAccountId, accountId)
                        .orderByAsc(WorkspaceShortcutPO::getSort)
                        .orderByAsc(WorkspaceShortcutPO::getId))
                .stream()
                .map(WorkspaceShortcutAssembler::toAggregate)
                .toList();
    }
}

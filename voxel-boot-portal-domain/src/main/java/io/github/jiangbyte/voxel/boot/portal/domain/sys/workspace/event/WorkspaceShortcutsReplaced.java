package io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.event;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作台快捷应用已全量替换。
 *
 * Author: Charlie
 */
public record WorkspaceShortcutsReplaced(
        String aggregateId,
        List<String> resourceIds,
        LocalDateTime occurredAt
) implements DomainEvent {

    public WorkspaceShortcutsReplaced(String accountId, List<String> resourceIds) {
        this(accountId, resourceIds, LocalDateTime.now());
    }
}

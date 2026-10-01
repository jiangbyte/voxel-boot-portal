package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.workspace;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.WorkspaceShortcut;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 工作台快捷应用聚合与 PO 互转。
 */
@Mapper
public interface WorkspaceShortcutAssembler {

    WorkspaceShortcutAssembler INSTANCE = Mappers.getMapper(WorkspaceShortcutAssembler.class);

    WorkspaceShortcutPO toPO(WorkspaceShortcut aggregate);


    public static WorkspaceShortcut toAggregate(WorkspaceShortcutPO po) {
        if (po == null) {
            return null;
        }
        return WorkspaceShortcut.reconstitute(po.getId(), po.getAccountId(), po.getResourceId(), po.getSort());
    }
}

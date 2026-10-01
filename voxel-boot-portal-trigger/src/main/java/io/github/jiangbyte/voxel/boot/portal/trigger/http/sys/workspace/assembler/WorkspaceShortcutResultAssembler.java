package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.workspace.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.result.WorkspaceShortcutResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceShortcutView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 WorkspaceShortcutView 装配为 API 响应 WorkspaceShortcutResult。
 */
@Mapper
public interface WorkspaceShortcutResultAssembler {

    WorkspaceShortcutResultAssembler INSTANCE = Mappers.getMapper(WorkspaceShortcutResultAssembler.class);

    WorkspaceShortcutResult toResult(WorkspaceShortcutView view);
}

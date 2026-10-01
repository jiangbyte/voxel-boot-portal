package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.workspace.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.result.WorkspaceOverviewResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceOverviewView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 WorkspaceOverviewView 装配为 API 响应 WorkspaceOverviewResult。
 */
@Mapper(uses = {
        WorkspaceShortcutResultAssembler.class,
        WorkspaceActivityItemResultAssembler.class
})
public interface WorkspaceOverviewResultAssembler {

    WorkspaceOverviewResultAssembler INSTANCE = Mappers.getMapper(WorkspaceOverviewResultAssembler.class);

    WorkspaceOverviewResult toResult(WorkspaceOverviewView view);
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.workspace.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.result.WorkspaceActivityItemResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.workspace.adapter.port.WorkspaceActivityItemView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 WorkspaceActivityItemView 装配为 API 响应 WorkspaceActivityItemResult。
 */
@Mapper
public interface WorkspaceActivityItemResultAssembler {

    WorkspaceActivityItemResultAssembler INSTANCE = Mappers.getMapper(WorkspaceActivityItemResultAssembler.class);

    WorkspaceActivityItemResult toResult(WorkspaceActivityItemView view);
}

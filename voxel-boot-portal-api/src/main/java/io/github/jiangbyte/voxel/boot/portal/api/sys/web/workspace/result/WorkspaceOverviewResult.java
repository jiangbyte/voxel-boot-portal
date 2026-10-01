package io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 工作台总览响应。
 *
 * Author: Charlie
 */
@Schema(description = "工作台总览")
@Data
public class WorkspaceOverviewResult {

    private List<WorkspaceShortcutResult> shortcuts;
    private List<WorkspaceActivityItemResult> recentOperations;
    private List<WorkspaceActivityItemResult> recentLogins;
}

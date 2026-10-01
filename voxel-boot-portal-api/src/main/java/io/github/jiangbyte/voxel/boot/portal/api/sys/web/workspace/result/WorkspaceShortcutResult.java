package io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工作台快捷应用响应。
 *
 * Author: Charlie
 */
@Schema(description = "工作台快捷应用")
@Data
public class WorkspaceShortcutResult {

    private String id;
    private String resourceId;
    private Integer sort;
    private String name;
    private String path;
    private String icon;
    private String code;
}

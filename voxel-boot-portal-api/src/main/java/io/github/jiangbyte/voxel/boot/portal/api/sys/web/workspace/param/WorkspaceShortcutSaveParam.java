package io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 全量保存工作台快捷应用入参。
 *
 * Author: Charlie
 */
@Schema(description = "工作台快捷应用保存入参")
@Data
public class WorkspaceShortcutSaveParam {

    @NotNull
    @Size(max = 16)
    @Schema(description = "资源 ID 列表，最多 16 个")
    private List<String> resourceIds;
}

package io.github.jiangbyte.voxel.boot.portal.api.sys.web.workspace.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作台近期日志摘要响应。
 *
 * Author: Charlie
 */
@Schema(description = "工作台近期日志摘要")
@Data
public class WorkspaceActivityItemResult {

    private String id;
    private String module;
    private String moduleLabel;
    private String action;
    private String actionName;
    private String actionType;
    private String summary;
    private Integer success;
    private String ip;
    private String userAgent;
    private String operatorName;
    private Integer durationMs;
    private String resourceId;
    private LocalDateTime createdAt;
}

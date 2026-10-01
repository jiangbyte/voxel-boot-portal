package io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 编辑公告入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑公告入参")
@Data
public class SysNoticeEditParam {

    @NotBlank
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "种类")
    private String kind = "ANNOUNCEMENT";

    @NotBlank
    @Schema(description = "标题")
    private String title;

    @NotBlank
    @Schema(description = "内容")
    private String content;

    @Schema(description = "内容格式")
    private String contentType = "TEXT";

    @Schema(description = "分类")
    private String category;

    @Schema(description = "重要等级")
    private String severity = "INFO";

    @Schema(description = "投放范围")
    private String targetScope = "ALL";

    @Schema(description = "目标账户类型 JSON")
    private String targetAccountTypes;

    @Schema(description = "目标账户ID JSON")
    private String targetAccountIds;

    @Schema(description = "目标部门ID JSON")
    private String targetDeptIds;

    @Schema(description = "目标角色ID JSON")
    private String targetRoleIds;

    @Schema(description = "发布位置 JSON")
    private String publishLocations;

    @Schema(description = "是否置顶")
    private Integer isPinned = 0;

    @Schema(description = "置顶截止")
    private LocalDateTime pinnedUntil;

    @Schema(description = "过期时间")
    private LocalDateTime expireAt;

    @Schema(description = "扩展信息")
    private String extra;
}

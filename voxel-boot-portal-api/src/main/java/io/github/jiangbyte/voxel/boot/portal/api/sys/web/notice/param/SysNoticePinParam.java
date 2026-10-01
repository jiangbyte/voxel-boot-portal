package io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告置顶入参。
 *
 * Author: Charlie
 */
@Schema(description = "公告置顶入参")
@Data
public class SysNoticePinParam {

    @NotBlank
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "是否置顶")
    private Integer isPinned = 1;

    @Schema(description = "置顶截止")
    private LocalDateTime pinnedUntil;
}

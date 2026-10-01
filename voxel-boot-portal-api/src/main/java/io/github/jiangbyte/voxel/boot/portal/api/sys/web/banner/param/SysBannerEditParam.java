package io.github.jiangbyte.voxel.boot.portal.api.sys.web.banner.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 编辑 Banner 入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑 Banner 入参")
@Data
public class SysBannerEditParam {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "主键ID")
    private String id;

    @NotBlank
    @Schema(description = "标题")
    private String title;

    @NotBlank
    @Schema(description = "Banner 图片 object_name 或 URL")
    private String image;

    @Schema(description = "点击跳转链接地址")
    private String url;

    @Schema(description = "链接类型（字典 BANNER_LINK_TYPE）")
    private String linkType = "URL";

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "描述说明")
    private String description;

    @NotBlank
    @Schema(description = "Banner 分类（字典 BANNER_CATEGORY）")
    private String category;

    @NotBlank
    @Schema(description = "Banner 类型（字典 BANNER_TYPE）")
    private String type;

    @NotBlank
    @Schema(description = "展示位置（字典 BANNER_POSITION）")
    private String position;

    @Schema(description = "可见账户类型：ADMIN/PORTAL")
    private List<String> targetAccountTypes = new ArrayList<>();

    @Schema(description = "排序号（越小越靠前）")
    private Integer sort = 0;

    @Schema(description = "Banner 状态：ENABLED/DISABLED")
    private String status = "ENABLED";

    @Schema(description = "开始展示时间")
    private LocalDateTime startAt;

    @Schema(description = "结束展示时间")
    private LocalDateTime endAt;
}

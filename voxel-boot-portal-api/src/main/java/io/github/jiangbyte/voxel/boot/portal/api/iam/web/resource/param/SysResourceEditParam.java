package io.github.jiangbyte.voxel.boot.portal.api.iam.web.resource.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 编辑资源入参。
 *
 * Author: Charlie
 */
@Schema(description = "编辑资源入参")
@Data
public class SysResourceEditParam {

    @NotBlank
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "父级资源ID")
    private String parentId;

    @NotBlank
    @Schema(description = "编码")
    private String code;

    @NotBlank
    @Schema(description = "名称")
    private String name;

    @Schema(description = "资源类型")
    private String resourceType = "MENU";

    @Schema(description = "所属模块ID")
    private String moduleId;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "前端组件")
    private String component;

    @Schema(description = "重定向")
    private String redirect;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "外链")
    private String href;

    @Schema(description = "排序号")
    private Integer sort = 99;

    @Schema(description = "是否可见")
    private Integer isVisible = 1;

    @Schema(description = "是否缓存")
    private Integer isCache = 0;

    @Schema(description = "是否固定标签")
    private Integer isAffix = 0;

    @Schema(description = "状态")
    private String status = "ENABLED";

    @Schema(description = "描述")
    private String description;

    @Schema(description = "布局")
    private String layout;

    @Schema(description = "扩展信息")
    private String extra;
}

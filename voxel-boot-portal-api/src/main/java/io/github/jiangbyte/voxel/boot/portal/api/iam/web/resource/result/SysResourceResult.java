package io.github.jiangbyte.voxel.boot.portal.api.iam.web.resource.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysResourceResult")
@Data
public class SysResourceResult {
    private String id;
    private String parentId;
    private String code;
    private String name;
    private String resourceType;
    private String moduleId;
    private String path;
    private String component;
    private String redirect;
    private String icon;
    private String color;
    private String href;
    private Integer sort;
    private Integer isVisible;
    private Integer isCache;
    private Integer isAffix;
    private String status;
    private String description;
    private String layout;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

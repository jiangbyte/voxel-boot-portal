package io.github.jiangbyte.voxel.boot.portal.api.sys.web.config.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysConfigResult")
@Data
public class SysConfigResult {
    private String id;
    private String configKey;
    private String configValue;
    private String category;
    private String remark;
    private Integer sortCode;
    private String valueType;
    private String label;
    private String scope;
    private String scene;
    private Integer sensitive;
    private Integer isBuiltin;
    private String extJson;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

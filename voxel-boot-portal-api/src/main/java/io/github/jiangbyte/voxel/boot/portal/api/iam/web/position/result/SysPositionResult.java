package io.github.jiangbyte.voxel.boot.portal.api.iam.web.position.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysPositionResult")
@Data
public class SysPositionResult {
    private String id;
    private String name;
    private String category;
    private String ownerDeptId;
    private Integer sort;
    private Integer isVirtual;
    private String status;
    private String description;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

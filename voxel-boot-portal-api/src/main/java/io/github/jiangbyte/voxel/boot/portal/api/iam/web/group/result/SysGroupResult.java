package io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysGroupResult")
@Data
public class SysGroupResult {
    private String id;
    private String name;
    private String ownerDeptId;
    private String description;
    private String status;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

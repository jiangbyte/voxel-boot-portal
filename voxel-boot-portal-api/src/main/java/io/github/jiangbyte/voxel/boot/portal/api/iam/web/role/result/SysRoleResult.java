package io.github.jiangbyte.voxel.boot.portal.api.iam.web.role.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysRoleResult")
@Data
public class SysRoleResult {
    private String id;
    private String code;
    private String name;
    private String category;
    private String scopeType;
    private String ownerDeptId;
    private Integer sort;
    private Integer isBuiltin;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

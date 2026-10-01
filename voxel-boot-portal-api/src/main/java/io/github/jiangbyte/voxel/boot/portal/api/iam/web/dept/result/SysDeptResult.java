package io.github.jiangbyte.voxel.boot.portal.api.iam.web.dept.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysDeptResult")
@Data
public class SysDeptResult {
    private String id;
    private String parentId;
    private String masterId;
    private String deputyMasterId;
    private String name;
    private String category;
    private Integer sort;
    private Integer isVirtual;
    private String status;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

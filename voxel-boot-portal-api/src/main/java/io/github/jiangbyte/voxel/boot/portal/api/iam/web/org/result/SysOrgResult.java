package io.github.jiangbyte.voxel.boot.portal.api.iam.web.org.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysOrgResult")
@Data
public class SysOrgResult {
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

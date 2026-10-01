package io.github.jiangbyte.voxel.boot.portal.api.sys.web.dict.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysDictResult")
@Data
public class SysDictResult {
    private String id;
    private String code;
    private String label;
    private String value;
    private String color;
    private String category;
    private String parentId;
    private String status;
    private Integer sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

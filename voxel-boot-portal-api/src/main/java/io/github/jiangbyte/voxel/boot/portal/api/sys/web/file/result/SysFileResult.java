package io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysFileResult")
@Data
public class SysFileResult {
    private String id;
    private String objectName;
    private String originalName;
    private String storageProvider;
    private String bucket;
    private String contentType;
    private Long size;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

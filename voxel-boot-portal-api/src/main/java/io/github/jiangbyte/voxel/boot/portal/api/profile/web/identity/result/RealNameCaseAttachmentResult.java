package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "实名附件")
public class RealNameCaseAttachmentResult {
    private String objectName;
    private String id;
    private String originalName;
    private String contentType;
    private Long size;
    private String url;
}

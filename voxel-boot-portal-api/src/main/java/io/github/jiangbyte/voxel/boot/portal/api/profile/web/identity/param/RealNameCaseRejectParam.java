package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "实名审核驳回")
public class RealNameCaseRejectParam {
    @NotBlank private String caseId;
    @NotBlank private String rejectReason;
}

package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "实名审核通过")
public class RealNameCaseApproveParam {
    @NotBlank private String caseId;
    private String remark;
}

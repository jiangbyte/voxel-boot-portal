package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "提交实名认证工单")
public class RealNameCaseSubmitParam {
    private String businessType;
    @NotBlank private String documentType;
    @NotBlank private String realName;
    @NotBlank private String documentNo;
    private List<String> attachmentIds = new ArrayList<>();
    private String applicantContact;
}

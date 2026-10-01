package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysAccountUpdateLoginIdentityParam {
    @NotBlank
    private String id;
    private Integer emailLoginEnabled = 0;
    private String email;
    private Integer phoneLoginEnabled = 0;
    private String phone;
}

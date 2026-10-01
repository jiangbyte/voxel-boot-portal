package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysAccountGrantRoleParam {
    @NotBlank
    private String id;
    private List<String> roleIds = new ArrayList<>();
}

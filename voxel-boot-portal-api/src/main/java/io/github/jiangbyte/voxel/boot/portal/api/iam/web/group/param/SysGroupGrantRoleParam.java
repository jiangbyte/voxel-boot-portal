package io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysGroupGrantRoleParam {
    @NotBlank
    private String id;
    private String accountType = "ADMIN";
    private List<String> roleIds = new ArrayList<>();
}

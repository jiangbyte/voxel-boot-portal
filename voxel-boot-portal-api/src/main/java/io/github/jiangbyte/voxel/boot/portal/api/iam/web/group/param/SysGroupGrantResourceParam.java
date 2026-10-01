package io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.param;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.grant.SysResourceGrantItemParam;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysGroupGrantResourceParam {
    @NotBlank
    private String id;
    private String accountType = "ADMIN";
    private List<SysResourceGrantItemParam> grantInfoList = new ArrayList<>();
}

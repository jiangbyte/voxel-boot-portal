package io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.param;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.grant.SysDeptGrantItemParam;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysAccountGrantDeptParam {
    @NotBlank
    private String id;
    private List<SysDeptGrantItemParam> grantInfoList = new ArrayList<>();
}

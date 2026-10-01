package io.github.jiangbyte.voxel.boot.portal.api.iam.web.group.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysGroupGrantUserParam {
    @NotBlank
    private String id;
    private List<String> accountIds = new ArrayList<>();
}

package io.github.jiangbyte.voxel.boot.portal.api.auth.web.session.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "按 Token 强制下线")
@Data
public class SessionTokenExitParam {
    @NotEmpty
    private List<String> tokens = new ArrayList<>();
}

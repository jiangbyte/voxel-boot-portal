package io.github.jiangbyte.voxel.boot.portal.api.auth.web.session.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "按账号强制下线")
@Data
public class SessionExitParam {
    @NotEmpty
    @Valid
    private List<Target> targets = new ArrayList<>();

    @Data
    public static class Target {
        @NotBlank
        private String accountId;
        private String accountType;
    }
}

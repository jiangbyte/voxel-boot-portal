package io.github.jiangbyte.voxel.boot.portal.api.sys.web.weakpassword.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "SysWeakPasswordResult")
@Data
public class SysWeakPasswordResult {
    private String id;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

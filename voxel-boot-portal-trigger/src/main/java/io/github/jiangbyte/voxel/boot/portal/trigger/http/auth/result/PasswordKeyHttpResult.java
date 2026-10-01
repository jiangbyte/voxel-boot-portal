package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.PasswordKeyResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "密码传输公钥响应")
@Data
public class PasswordKeyHttpResult {
    private String keyId;
    private String publicKey;

    public static PasswordKeyHttpResult from(PasswordKeyResult result) {
        if (result == null) return null;
        PasswordKeyHttpResult http = new PasswordKeyHttpResult();
        http.setKeyId(result.getKeyId());
        http.setPublicKey(result.getPublicKey());
        return http;
    }
}

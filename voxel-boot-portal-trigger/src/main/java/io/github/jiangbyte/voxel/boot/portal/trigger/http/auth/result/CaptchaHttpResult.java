package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.CaptchaResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "验证码响应")
@Data
public class CaptchaHttpResult {
    private String captchaId;
    private String imageType;
    private String imageBase64;

    public static CaptchaHttpResult from(CaptchaResult result) {
        if (result == null) return null;
        CaptchaHttpResult http = new CaptchaHttpResult();
        http.setCaptchaId(result.getCaptchaId());
        http.setImageType(result.getImageType());
        http.setImageBase64(result.getImageBase64());
        return http;
    }
}

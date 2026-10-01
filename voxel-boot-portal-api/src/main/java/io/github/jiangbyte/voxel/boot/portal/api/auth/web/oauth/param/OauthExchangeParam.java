package io.github.jiangbyte.voxel.boot.portal.api.auth.web.oauth.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "OAuth 换票参数")
@Data
public class OauthExchangeParam {
    @NotBlank
    @Schema(description = "一次性兑换码")
    private String code;
}

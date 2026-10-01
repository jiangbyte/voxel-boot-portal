package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.site;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.api.auth.web.site.result.SiteFooterResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

/**
 * 站点页脚公开接口：版权与备案，供 Admin / Portal 等前端统一拉取。
 *
 * Author: Charlie
 */
@Tag(name = "站点页脚公开接口")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicSiteFooterController {

    private final ConfigApi configApi;


    /** 查询站点页脚配置。 */
    @Operation(summary = "查询站点页脚")
    @GetMapping("/v1/public/site-footer")
    public ApiResponse<SiteFooterResult> siteFooter() {
        return ApiResponse.ok(resolve(configApi));
    }

    /**
     * 从系统配置解析页脚字段。
     */
    private static SiteFooterResult resolve(ConfigApi config) {
        // 1. 空配置返回全空默认值
        SiteFooterResult result = new SiteFooterResult();
        if (config == null) {
            return result;
        }
        // 2. 按约定 key 读取并 trim
        result.setCopyrightText(trim(config.getByKey("COPYRIGHT_TEXT", "")));
        result.setCopyrightUrl(trim(config.getByKey("COPYRIGHT_URL", "")));
        result.setIcpNumber(trim(config.getByKey("SITE_ICP_NUMBER", "")));
        result.setIcpUrl(trim(config.getByKey("SITE_ICP_URL", "")));
        result.setPsbNumber(trim(config.getByKey("SITE_PSB_NUMBER", "")));
        result.setPsbUrl(trim(config.getByKey("SITE_PSB_URL", "")));
        return result;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}

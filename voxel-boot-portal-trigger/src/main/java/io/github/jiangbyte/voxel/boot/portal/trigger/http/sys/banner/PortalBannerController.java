package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.banner;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.banner.assembler.SysBannerResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.banner.IBannerCase;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerView;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.banner.result.SysBannerResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.github.jiangbyte.voxel.boot.portal.api.web.IdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import lombok.RequiredArgsConstructor;

/**
 * 门户端 Banner API：可见列表与互动计数。
 *
 * Author: Charlie
 */
@Tag(name = "门户端 Banner API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalBannerController {

    private final BannerQueryService bannerQueryService;
    private final IBannerCase bannerApplicationService;


    /** 门户可见 Banner 列表。 */
    @Operation(summary = "门户可见 Banner 列表")
    @GetMapping("/v1/portal/sys/banner/list")
    public ApiResponse<List<SysBannerResult>> list(@RequestParam String position,
                                                   @RequestParam(required = false) String category,
                                                   @RequestParam(required = false) String type) {
        List<BannerView> views = bannerQueryService.portalList(position, category, type);
        return ApiResponse.ok(views.stream().map(SysBannerResultAssembler.INSTANCE::toResult).toList());
    }

    /** Banner 互动计数。 */
    @Operation(summary = "Banner 互动计数")
    @PostMapping("/v1/portal/sys/banner/interaction")
    public ApiResponse<Void> interaction(@Valid @RequestBody IdParam param) {
        bannerApplicationService.interaction(param.getId());
        return ApiResponse.ok();
    }
}

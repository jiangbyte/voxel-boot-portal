package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.resource;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.resource.assembler.SysResourceResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.resource.adapter.port.ResourceAdminExtrasService;
import io.github.jiangbyte.voxel.boot.portal.api.iam.web.resource.result.SysResourceResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Tag(name = "门户端资源 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalResourceController {

    private final ResourceAdminExtrasService extrasService;


    @Operation(summary = "当前门户可见资源")
    @GetMapping("/v1/portal/iam/resource/current")
    public ApiResponse<List<SysResourceResult>> current() {
        return ApiResponse.ok(extrasService.listPortalCurrent().stream().map(SysResourceResultAssembler.INSTANCE::toResult).toList());
    }
}

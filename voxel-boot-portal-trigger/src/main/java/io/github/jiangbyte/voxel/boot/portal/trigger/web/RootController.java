package io.github.jiangbyte.voxel.boot.portal.trigger.web;

import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 根路径探针：确认应用已启动。
 *
 * Author: Charlie
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ApiResponse<Map<String, String>> root() {
        return ApiResponse.ok(Map.of(
                "name", "voxel-boot-ddd",
                "status", "ok"));
    }
}

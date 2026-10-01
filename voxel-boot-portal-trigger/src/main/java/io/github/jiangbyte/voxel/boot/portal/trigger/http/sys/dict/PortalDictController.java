package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.dict;

import cn.hutool.core.lang.tree.Tree;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictQueryService;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 门户端数据字典 API：按分类查询字典树。
 *
 * Author: Charlie
 */
@Tag(name = "门户端数据字典 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalDictController {

    private final DictQueryService dictQueryService;


    /** 树形查询。 */
    @Operation(summary = "树形查询")
    @GetMapping("/v1/portal/sys/dict/tree")
    public ApiResponse<List<Tree<String>>> tree(@RequestParam(required = false) String category) {
        return ApiResponse.ok(dictQueryService.tree(category));
    }
}

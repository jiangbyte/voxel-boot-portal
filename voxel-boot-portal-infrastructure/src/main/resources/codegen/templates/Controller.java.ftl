package ${basePackage}.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.jiangbyte.io.common.core.domain.ApiResponse;
import github.jiangbyte.io.common.core.param.IdParam;
import github.jiangbyte.io.common.core.param.IdsParam;
import github.jiangbyte.io.common.log.annotation.OperationAudit;
import github.jiangbyte.io.common.satoken.StpKit;
import ${basePackage}.entity.${entityName};
import ${paramPackage}.${entityName}AddParam;
import ${paramPackage}.${entityName}EditParam;
import ${paramPackage}.${entityName}PageParam;
import ${basePackage}.service.${entityName}Service;
<#if hasSub && subEntityName??>
import ${basePackage}.entity.${subEntityName};
import ${paramPackage}.${subEntityName}AddParam;
import ${paramPackage}.${subEntityName}EditParam;
import ${paramPackage}.${subEntityName}PageParam;
</#if>
<#if hasTree>
import cn.hutool.core.lang.tree.Tree;
</#if>
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端 ${entityName} API：CRUD<#if hasTree>与树查询</#if><#if hasSub && subEntityName??>与子实体维护</#if>。
 *
 * Author: ${author}
 */
@Tag(name = "管理端 ${businessName} API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Admin${entityName}Controller {

    private final ${entityName}Service ${varName}Service;

    /** 创建。 */
    @Operation(summary = "创建。")
    @PostMapping("/v1/admin${apiPrefix}/create")
    @SaCheckPermission(value = "${permissionPrefix}:create", type = StpKit.TYPE_ADMIN)
    @OperationAudit(resourceType = "${auditResourceType}", action = "create")
    public ApiResponse<Void> create(@Valid @RequestBody ${entityName}AddParam param) {
        ${varName}Service.create(param);
        return ApiResponse.ok();
    }

    /** 更新。 */
    @Operation(summary = "更新。")
    @PostMapping("/v1/admin${apiPrefix}/update")
    @SaCheckPermission(value = "${permissionPrefix}:update", type = StpKit.TYPE_ADMIN)
    @OperationAudit(resourceType = "${auditResourceType}", action = "update")
    public ApiResponse<Void> update(@Valid @RequestBody ${entityName}EditParam param) {
        ${varName}Service.update(param);
        return ApiResponse.ok();
    }

    /** 批量删除。 */
    @Operation(summary = "批量删除。")
    @PostMapping("/v1/admin${apiPrefix}/delete")
    @SaCheckPermission(value = "${permissionPrefix}:delete", type = StpKit.TYPE_ADMIN)
    @OperationAudit(resourceType = "${auditResourceType}", action = "delete")
    public ApiResponse<Void> delete(@Valid @RequestBody IdsParam param) {
        ${varName}Service.delete(param);
        return ApiResponse.ok();
    }

    /** 查询详情。 */
    @Operation(summary = "查询详情。")
    @GetMapping("/v1/admin${apiPrefix}/detail")
    @SaCheckPermission(value = "${permissionPrefix}:detail", type = StpKit.TYPE_ADMIN)
    public ApiResponse<${entityName}> detail(@Valid @ModelAttribute IdParam param) {
        return ApiResponse.ok(${varName}Service.detail(param.getId()));
    }

    /** 分页查询。 */
    @Operation(summary = "分页查询。")
    @GetMapping("/v1/admin${apiPrefix}/page")
    @SaCheckPermission(value = "${permissionPrefix}:page", type = StpKit.TYPE_ADMIN)
    public ApiResponse<Page<${entityName}>> page(@Valid @ModelAttribute ${entityName}PageParam param) {
        return ApiResponse.ok(${varName}Service.page(param));
    }
<#if hasTree>

    /** 树形查询。 */
    @Operation(summary = "树形查询。")
    @GetMapping("/v1/admin${apiPrefix}/tree")
    @SaCheckPermission(value = "${permissionPrefix}:tree", type = StpKit.TYPE_ADMIN)
    public ApiResponse<List<Tree<String>>> tree(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(${varName}Service.tree(keyword));
    }
</#if>
<#if hasSub && subEntityName??>

    /** 创建子项。 */
    @Operation(summary = "创建子项。")
    @PostMapping("/v1/admin${apiPrefix}/children/create")
    @SaCheckPermission(value = "${permissionPrefix}:create", type = StpKit.TYPE_ADMIN)
    @OperationAudit(resourceType = "${auditResourceType}", action = "create")
    public ApiResponse<Void> childCreate(@Valid @RequestBody ${subEntityName}AddParam param) {
        ${varName}Service.childCreate(param);
        return ApiResponse.ok();
    }

    /** 更新子项。 */
    @Operation(summary = "更新子项。")
    @PostMapping("/v1/admin${apiPrefix}/children/update")
    @SaCheckPermission(value = "${permissionPrefix}:update", type = StpKit.TYPE_ADMIN)
    @OperationAudit(resourceType = "${auditResourceType}", action = "update")
    public ApiResponse<Void> childUpdate(@Valid @RequestBody ${subEntityName}EditParam param) {
        ${varName}Service.childUpdate(param);
        return ApiResponse.ok();
    }

    /** 删除子项。 */
    @Operation(summary = "删除子项。")
    @PostMapping("/v1/admin${apiPrefix}/children/delete")
    @SaCheckPermission(value = "${permissionPrefix}:delete", type = StpKit.TYPE_ADMIN)
    @OperationAudit(resourceType = "${auditResourceType}", action = "delete")
    public ApiResponse<Void> childDelete(@Valid @RequestBody IdsParam param) {
        ${varName}Service.childDelete(param);
        return ApiResponse.ok();
    }

    /** 查询子项详情。 */
    @Operation(summary = "查询子项详情。")
    @GetMapping("/v1/admin${apiPrefix}/children/detail")
    @SaCheckPermission(value = "${permissionPrefix}:detail", type = StpKit.TYPE_ADMIN)
    public ApiResponse<${subEntityName}> childDetail(@Valid @ModelAttribute IdParam param) {
        return ApiResponse.ok(${varName}Service.childDetail(param.getId()));
    }

    /** 分页查询子项。 */
    @Operation(summary = "分页查询子项。")
    @GetMapping("/v1/admin${apiPrefix}/children/page")
    @SaCheckPermission(value = "${permissionPrefix}:page", type = StpKit.TYPE_ADMIN)
    public ApiResponse<Page<${subEntityName}>> childPage(@Valid @ModelAttribute ${subEntityName}PageParam param) {
        return ApiResponse.ok(${varName}Service.childPage(param));
    }
</#if>
}
package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.audit;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.audit.assembler.SysAuditResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.audit.param.SysAuditPageParam;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.audit.result.SysAuditResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 门户端操作审计 API（仅本人）。
 *
 * Author: Charlie
 */
@Tag(name = "门户端操作审计 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalAuditController {

    private final AuditQueryService auditQueryService;
    private final SessionPort sessionPort;


    @Operation(summary = "当前用户本人审计日志分页")
    @GetMapping("/v1/portal/sys/audit/my-page")
    public ApiResponse<PageResult<SysAuditResult>> myPage(@Valid @ModelAttribute SysAuditPageParam param) {
        String accountId = requirePortalAccountId();
        PageResult<AuditView> page = auditQueryService.myPage(accountId, toQuery(param));
        return ApiResponse.ok(mapPage(page));
    }

    @Operation(summary = "当前用户本人审计详情")
    @GetMapping("/v1/portal/sys/audit/my-detail")
    public ApiResponse<SysAuditResult> myDetail(@RequestParam String id) {
        String accountId = requirePortalAccountId();
        return ApiResponse.ok(SysAuditResultAssembler.INSTANCE.toResult(auditQueryService.myDetail(id, accountId)));
    }

    private String requirePortalAccountId() {
        String accountId = sessionPort.currentPortalAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "未登录");
        }
        return accountId;
    }

    private static AuditPageQuery toQuery(SysAuditPageParam param) {
        return new AuditPageQuery(param.getCurrent(), param.getSize(), param.getModule(),
                param.getAction(), param.getExcludeAction(), param.getAccountId(), param.getSuccess());
    }

    private static PageResult<SysAuditResult> mapPage(PageResult<AuditView> page) {
        List<SysAuditResult> records = page.records().stream().map(SysAuditResultAssembler.INSTANCE::toResult).toList();
        return new PageResult<>(records, page.total(), page.current(), page.size());
    }
}

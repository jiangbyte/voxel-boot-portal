package io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler.RealNameCaseSummaryResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler.RealNameCaseOptionsResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler.RealNameCaseInitResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.profile.identity.assembler.IdentityStatusResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.IProfileIdentityCase;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.IRealNameCase;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.identity.RealNameCaseSummaryView;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param.RealNameCaseCallbackParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param.RealNameCaseInitThirdPartyParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param.RealNameCaseMyPageParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.param.RealNameCaseSubmitParam;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.IdentityStatusResult;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseInitResult;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseOptionsResult;
import io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result.RealNameCaseSummaryResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 门户端用户实名认证 API。
 *
 * Author: Charlie
 */
@Tag(name = "门户端用户实名认证 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalRealNameUserController {

    private final IProfileIdentityCase profileIdentityApplicationService;
    private final IRealNameCase realNameCaseApplicationService;
    private final SessionPort sessionPort;


    @Operation(summary = "查询当前账号实名状态")
    @GetMapping("/v1/portal/profile/identity/status")
    public ApiResponse<IdentityStatusResult> identityStatus() {
        return ApiResponse.ok(IdentityStatusResultAssembler.INSTANCE.toResult(
                profileIdentityApplicationService.getUserStatusForAccount(requirePortalAccountId())));
    }

    @Operation(summary = "查询实名选项")
    @GetMapping("/v1/portal/profile/identity/case/options")
    public ApiResponse<RealNameCaseOptionsResult> options() {
        return ApiResponse.ok(RealNameCaseOptionsResultAssembler.INSTANCE.toResult(realNameCaseApplicationService.options()));
    }

    @Operation(summary = "提交人工实名工单")
    @PostMapping("/v1/portal/profile/identity/case/submit")
    public ApiResponse<Void> submit(@Valid @RequestBody RealNameCaseSubmitParam param) {
        realNameCaseApplicationService.submit(
                requirePortalAccountId(),
                param.getBusinessType(),
                param.getDocumentType(),
                param.getRealName(),
                param.getDocumentNo(),
                param.getAttachmentIds(),
                param.getApplicantContact());
        return ApiResponse.ok();
    }

    @Operation(summary = "初始化第三方实名")
    @PostMapping("/v1/portal/profile/identity/case/init-third-party")
    public ApiResponse<RealNameCaseInitResult> initThirdParty(@Valid @RequestBody RealNameCaseInitThirdPartyParam param) {
        return ApiResponse.ok(RealNameCaseInitResultAssembler.INSTANCE.toResult(realNameCaseApplicationService.initThirdParty(
                requirePortalAccountId(),
                param.getBusinessType(),
                param.getDocumentType(),
                param.getRealName(),
                param.getDocumentNo(),
                param.getProvider())));
    }

    @Operation(summary = "第三方实名回调")
    @PostMapping("/v1/portal/profile/identity/case/callback")
    public ApiResponse<Void> callback(@Valid @RequestBody RealNameCaseCallbackParam param) {
        realNameCaseApplicationService.callback(
                param.getCaseId(), param.getProviderOrderNo(), param.getSuccess(), param.getMessage());
        return ApiResponse.ok();
    }

    @Operation(summary = "查询本人实名工单分页")
    @GetMapping("/v1/portal/profile/identity/case/my-page")
    public ApiResponse<PageResult<RealNameCaseSummaryResult>> myPage(@Valid @ModelAttribute RealNameCaseMyPageParam param) {
        PageResult<RealNameCaseSummaryView> page = realNameCaseApplicationService.myPage(
                requirePortalAccountId(), param.getBusinessType(), param.getStatus(), param.getCurrent(), param.getSize());
        List<RealNameCaseSummaryResult> records = page.records().stream().map(RealNameCaseSummaryResultAssembler.INSTANCE::toResult).toList();
        return ApiResponse.ok(new PageResult<>(records, page.total(), page.current(), page.size()));
    }

    private String requirePortalAccountId() {
        String accountId = sessionPort.currentPortalAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "Not logged in");
        }
        return accountId;
    }
}

package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.feedback;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.feedback.assembler.SysFeedbackResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.feedback.IFeedbackCase;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port.FeedbackView;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.feedback.SubmitFeedbackCommand;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.feedback.param.SysFeedbackAddParam;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.feedback.param.SysFeedbackPageParam;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.feedback.result.SysFeedbackResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.github.jiangbyte.voxel.boot.portal.api.web.IdParam;
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
 * 门户端反馈 API：当前用户提交并查看本人反馈。
 *
 * Author: Charlie
 */
@Tag(name = "门户端反馈 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalFeedbackController {

    private static final String ACCOUNT_TYPE_PORTAL = "PORTAL";

    private final IFeedbackCase feedbackApplicationService;
    private final FeedbackQueryService feedbackQueryService;
    private final SessionPort sessionPort;


    /** 门户用户提交反馈。 */
    @Operation(summary = "门户用户提交反馈")
    @PostMapping("/v1/portal/sys/feedback/submit")
    public ApiResponse<Void> submit(@Valid @RequestBody SysFeedbackAddParam param) {
        String accountId = requirePortalAccountId();
        feedbackApplicationService.submit(new SubmitFeedbackCommand(
                param.getTitle(),
                param.getContent(),
                param.getCategory(),
                param.getContact(),
                param.getAttachObjectNames(),
                ACCOUNT_TYPE_PORTAL,
                accountId));
        return ApiResponse.ok();
    }

    /** 分页查询当前门户用户提交的反馈。 */
    @Operation(summary = "分页查询当前门户用户提交的反馈")
    @GetMapping("/v1/portal/sys/feedback/my-page")
    public ApiResponse<PageResult<SysFeedbackResult>> myPage(@Valid @ModelAttribute SysFeedbackPageParam param) {
        String accountId = requirePortalAccountId();
        PageResult<FeedbackView> page = feedbackQueryService.myPage(
                ACCOUNT_TYPE_PORTAL, accountId,
                new FeedbackPageQuery(param.getCurrent(), param.getSize(), null, null, null, null));
        List<SysFeedbackResult> records = page.records().stream().map(SysFeedbackResultAssembler.INSTANCE::toResult).toList();
        return ApiResponse.ok(new PageResult<>(records, page.total(), page.current(), page.size()));
    }

    /** 查询当前门户用户本人的反馈详情。 */
    @Operation(summary = "查询当前门户用户本人的反馈详情")
    @GetMapping("/v1/portal/sys/feedback/my-detail")
    public ApiResponse<SysFeedbackResult> myDetail(@Valid @ModelAttribute IdParam param) {
        String accountId = requirePortalAccountId();
        FeedbackView view = feedbackQueryService.myDetail(param.getId(), ACCOUNT_TYPE_PORTAL, accountId);
        return ApiResponse.ok(SysFeedbackResultAssembler.INSTANCE.toResult(view));
    }

    private String requirePortalAccountId() {
        String accountId = sessionPort.currentPortalAccountId();
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException(401, "Not logged in");
        }
        return accountId;
    }
}

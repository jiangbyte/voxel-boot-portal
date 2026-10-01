package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.notice.assembler.SysNoticeResultAssembler;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticePageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticeQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port.NoticeView;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.param.SysNoticePageParam;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.notice.result.SysNoticeResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.github.jiangbyte.voxel.boot.portal.api.web.IdParam;
import io.github.jiangbyte.voxel.boot.portal.api.web.IdsParam;
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
 * 门户端公告/消息 API。
 *
 * Author: Charlie
 */
@Tag(name = "门户端公告 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalNoticeController {

    private final NoticeQueryService noticeQueryService;


    /** 门户已发布公告列表。 */
    @Operation(summary = "门户已发布公告列表")
    @GetMapping("/v1/portal/sys/notice/list")
    public ApiResponse<PageResult<SysNoticeResult>> list(@Valid @ModelAttribute SysNoticePageParam param) {
        return ApiResponse.ok(toPage(noticeQueryService.portalList(toQuery(param))));
    }

    /** 我的消息分页。 */
    @Operation(summary = "我的消息分页")
    @GetMapping("/v1/portal/sys/notice/my-page")
    public ApiResponse<PageResult<SysNoticeResult>> myPage(@Valid @ModelAttribute SysNoticePageParam param) {
        return ApiResponse.ok(toPage(noticeQueryService.myPage(toQuery(param))));
    }

    /** 我的消息详情。 */
    @Operation(summary = "我的消息详情")
    @GetMapping("/v1/portal/sys/notice/my-detail")
    public ApiResponse<SysNoticeResult> myDetail(@Valid @ModelAttribute IdParam param) {
        return ApiResponse.ok(SysNoticeResultAssembler.INSTANCE.toResult(noticeQueryService.myDetail(param.getId())));
    }

    /** 未读数量。 */
    @Operation(summary = "未读数量")
    @GetMapping("/v1/portal/sys/notice/unread-count")
    public ApiResponse<Integer> unreadCount() {
        return ApiResponse.ok(noticeQueryService.unreadCount());
    }

    /** 标记已读。 */
    @Operation(summary = "标记已读")
    @PostMapping("/v1/portal/sys/notice/read")
    public ApiResponse<Void> read(@Valid @RequestBody IdsParam param) {
        noticeQueryService.markRead(param.getIds());
        return ApiResponse.ok();
    }

    /** 全部已读。 */
    @Operation(summary = "全部已读")
    @PostMapping("/v1/portal/sys/notice/read-all")
    public ApiResponse<Void> readAll() {
        noticeQueryService.markAllRead();
        return ApiResponse.ok();
    }

    private static NoticePageQuery toQuery(SysNoticePageParam param) {
        return new NoticePageQuery(
                param.getCurrent(),
                param.getSize(),
                param.getKind(),
                param.getTitle(),
                param.getCategory(),
                param.getStatus(),
                param.getSeverity());
    }

    private static PageResult<SysNoticeResult> toPage(PageResult<NoticeView> page) {
        List<SysNoticeResult> records = page.records().stream().map(SysNoticeResultAssembler.INSTANCE::toResult).toList();
        return new PageResult<>(records, page.total(), page.current(), page.size());
    }
}

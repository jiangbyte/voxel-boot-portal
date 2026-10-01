package io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

/**
 * 反馈查询用例端口：详情、分页与「我的」查询。
 *
 * Author: Charlie
 */
public interface FeedbackQueryService {

    /**
     * 管理端按主键查询详情。
     */
    FeedbackView detail(String id);

    /**
     * 管理端分页查询全部反馈。
     */
    PageResult<FeedbackView> page(FeedbackPageQuery query);

    /**
     * 当前用户提交的反馈分页。
     */
    PageResult<FeedbackView> myPage(String accountType, String accountId, FeedbackPageQuery query);

    /**
     * 当前用户本人的反馈详情（含归属校验）。
     */
    FeedbackView myDetail(String id, String accountType, String accountId);
}

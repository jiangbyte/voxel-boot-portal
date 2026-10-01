package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 公告查询用例端口。
 *
 * Author: Charlie
 */
public interface NoticeQueryService {

    /**
     * 按主键查询详情。
     */
    NoticeView detail(String id);

    /**
     * 管理端分页。
     */
    PageResult<NoticeView> page(NoticePageQuery query);

    /**
     * 门户已发布公告分页（默认可匿名；kind 固定 ANNOUNCEMENT）。
     */
    PageResult<NoticeView> portalList(NoticePageQuery query);

    /**
     * 我的已发布消息分页（需登录）。
     */
    PageResult<NoticeView> myPage(NoticePageQuery query);

    /**
     * 我的消息详情（需登录；公告会累加浏览并标记已读）。
     */
    NoticeView myDetail(String id);

    /**
     * 未读数量。
     */
    int unreadCount();

    /**
     * 标记指定消息已读。
     */
    void markRead(List<String> ids);

    /**
     * 标记全部可见已发布消息已读。
     */
    void markAllRead();

    /**
     * 列出对当前用户可见的已发布消息 ID（分页批处理用）。
     */
    List<String> listVisiblePublishedIds(String accountType, String accountId, String kind, long offset, int limit);
}

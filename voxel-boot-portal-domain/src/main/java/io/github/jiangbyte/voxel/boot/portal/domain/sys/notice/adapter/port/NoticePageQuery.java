package io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.port;

/**
 * 公告分页查询。
 *
 * Author: Charlie
 */
public record NoticePageQuery(
        long current,
        long size,
        String kind,
        String title,
        String category,
        String status,
        String severity
) {
}

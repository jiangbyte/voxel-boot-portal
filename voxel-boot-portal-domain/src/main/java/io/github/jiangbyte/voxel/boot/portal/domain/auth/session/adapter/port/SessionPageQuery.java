package io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port;

/**
 * 在线会话分页查询。
 *
 * Author: Charlie
 */
public record SessionPageQuery(
        long current,
        long size,
        String accountType,
        String accountId,
        String account,
        String ip,
        String keyword
) {
}

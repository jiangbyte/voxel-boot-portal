package io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port;

/**
 * 按账号强制下线目标。
 *
 * Author: Charlie
 */
public record SessionExitTarget(String accountId, String accountType) {
}

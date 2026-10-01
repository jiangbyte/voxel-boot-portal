package io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;

import java.util.List;

/**
 * 管理端在线会话查询与强制下线端口（Sa-Token StpKit）。
 *
 * Author: Charlie
 */
public interface SessionAdminPort {

    SessionAnalysisView analyze();

    PageResult<SessionAccountView> page(SessionPageQuery query);

    List<SessionTokenView> tokens(String accountId, String accountType);

    void exitByAccounts(List<SessionExitTarget> targets);

    void exitByTokens(List<String> tokens);
}

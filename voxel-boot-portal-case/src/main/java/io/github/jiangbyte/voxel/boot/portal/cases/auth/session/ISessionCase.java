package io.github.jiangbyte.voxel.boot.portal.cases.auth.session;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAccountView;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAdminPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAnalysisView;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionExitTarget;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionTokenView;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface ISessionCase {
    SessionAnalysisView analysis();
    PageResult<SessionAccountView> page(SessionPageQuery query);
    List<SessionTokenView> tokens(String accountId, String accountType);
    void exit(List<SessionExitTarget> targets);
    void tokenExit(List<String> tokens);
}

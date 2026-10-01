package io.github.jiangbyte.voxel.boot.portal.cases.auth.session;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAccountView;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAdminPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAnalysisView;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionExitTarget;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionTokenView;
import lombok.RequiredArgsConstructor;

/**
 * 管理端在线会话用例：统计、分页、Token 列表与强制下线。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class SessionCaseImpl implements ISessionCase {

    private final SessionAdminPort sessionAdminPort;


    public SessionAnalysisView analysis() {
        return sessionAdminPort.analyze();
    }

    public PageResult<SessionAccountView> page(SessionPageQuery query) {
        return sessionAdminPort.page(query);
    }

    public List<SessionTokenView> tokens(String accountId, String accountType) {
        return sessionAdminPort.tokens(accountId, accountType);
    }

    public void exit(List<SessionExitTarget> targets) {
        sessionAdminPort.exitByAccounts(targets);
    }

    public void tokenExit(List<String> tokens) {
        sessionAdminPort.exitByTokens(tokens);
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAccountView;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAdminPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionAnalysisView;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionExitTarget;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionPageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port.SessionTokenView;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 基于 StpKit 的在线会话管理实现。
 *
 * Author: Charlie
 */
@Component
public class SessionAdminPortImpl implements SessionAdminPort {

    private static final int SCAN_HARD_CAP = 5000;
    private static final int SCAN_BATCH = 200;
    private static final int ANALYSIS_SCAN_CAP = 5000;

    @Override
    public SessionAnalysisView analyze() {
        // 1. 分别扫描管理端/门户会话并汇总
        SessionAnalysisView result = new SessionAnalysisView();
        AnalysisAccumulator admin = analyzeLogic(StpKit.ADMIN);
        AnalysisAccumulator portal = analyzeLogic(StpKit.PORTAL);
        result.setOnlineAccountCount(admin.accountCount + portal.accountCount);
        result.setOnlineTokenCount(admin.tokenCount + portal.tokenCount);
        result.setAdminAccountCount(admin.accountCount);
        result.setPortalAccountCount(portal.accountCount);
        result.setOneHourNewCount(admin.oneHourNew + portal.oneHourNew);
        result.setMaxTokenCount(Math.max(admin.maxToken, portal.maxToken));
        return result;
    }

    @Override
    public PageResult<SessionAccountView> page(SessionPageQuery query) {
        // 1. 规范化分页参数
        int current = (int) Math.max(1, query.current());
        int size = (int) Math.max(1, Math.min(query.size(), 200));
        boolean filtered = StringUtils.hasText(query.accountId())
                || StringUtils.hasText(query.account())
                || StringUtils.hasText(query.ip())
                || StringUtils.hasText(query.keyword());
        List<LogicSource> sources = resolveSources(query.accountType());
        if (sources.isEmpty()) {
            return new PageResult<>(List.of(), 0, current, size);
        }
        // 2. 无过滤且单端：走 Sa-Token 原生分页
        if (!filtered && sources.size() == 1) {
            return pageSingleSource(sources.getFirst(), current, size);
        }
        // 3. 多端或带过滤：有限扫描后内存过滤分页
        List<SessionAccountView> matched = new ArrayList<>();
        int scanned = 0;
        boolean hasMore = false;
        for (LogicSource source : sources) {
            int offset = 0;
            while (scanned < SCAN_HARD_CAP) {
                int batch = Math.min(SCAN_BATCH, SCAN_HARD_CAP - scanned);
                List<String> sessionIds = source.stpLogic().searchSessionId("", offset, batch, false);
                if (sessionIds == null || sessionIds.isEmpty()) {
                    break;
                }
                for (String sessionId : sessionIds) {
                    SessionAccountView item = hydrateSession(source.stpLogic(), sessionId, source.accountType());
                    if (item == null) {
                        continue;
                    }
                    if (matchesFilter(item, query)) {
                        matched.add(item);
                    }
                }
                scanned += sessionIds.size();
                offset += sessionIds.size();
                if (sessionIds.size() < batch) {
                    break;
                }
                if (scanned >= SCAN_HARD_CAP) {
                    hasMore = true;
                    break;
                }
            }
            if (hasMore) {
                break;
            }
        }
        int from = Math.max(0, (current - 1) * size);
        int to = Math.min(matched.size(), from + size);
        List<SessionAccountView> records = from >= matched.size() ? List.of() : matched.subList(from, to);
        long total = matched.size();
        if (hasMore) {
            total = Math.max(total, (long) current * size + (records.size() < size ? 0 : 1));
        }
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public List<SessionTokenView> tokens(String accountId, String accountType) {
        return listTokens(resolveLogic(accountType), accountId, accountType).tokens();
    }

    @Override
    public void exitByAccounts(List<SessionExitTarget> targets) {
        if (targets == null) {
            return;
        }
        for (SessionExitTarget target : targets) {
            if (target == null || !StringUtils.hasText(target.accountId())) {
                continue;
            }
            resolveLogic(target.accountType()).logout(target.accountId());
        }
    }

    @Override
    public void exitByTokens(List<String> tokens) {
        if (tokens == null) {
            return;
        }
        LinkedHashSet<String> unique = new LinkedHashSet<>(tokens);
        for (String token : unique) {
            if (!StringUtils.hasText(token)) {
                continue;
            }
            StpKit.ADMIN.logoutByTokenValue(token);
            StpKit.PORTAL.logoutByTokenValue(token);
        }
    }

    private PageResult<SessionAccountView> pageSingleSource(LogicSource source, int current, int size) {
        int start = (current - 1) * size;
        List<String> sessionIds = source.stpLogic().searchSessionId("", start, size, false);
        if (sessionIds == null) {
            sessionIds = List.of();
        }
        List<SessionAccountView> records = new ArrayList<>(sessionIds.size());
        for (String sessionId : sessionIds) {
            SessionAccountView item = hydrateSession(source.stpLogic(), sessionId, source.accountType());
            if (item != null) {
                records.add(item);
            }
        }
        long total = start + records.size();
        if (sessionIds.size() >= size) {
            List<String> probe = source.stpLogic().searchSessionId("", start + size, 1, false);
            if (probe != null && !probe.isEmpty()) {
                total = start + size + 1;
            }
        }
        return new PageResult<>(records, total, current, size);
    }

    private List<LogicSource> resolveSources(String accountType) {
        List<LogicSource> sources = new ArrayList<>(2);
        if (!StringUtils.hasText(accountType) || AccountType.ADMIN.name().equalsIgnoreCase(accountType)) {
            sources.add(new LogicSource(StpKit.ADMIN, AccountType.ADMIN.name()));
        }
        if (!StringUtils.hasText(accountType) || AccountType.PORTAL.name().equalsIgnoreCase(accountType)) {
            sources.add(new LogicSource(StpKit.PORTAL, AccountType.PORTAL.name()));
        }
        return sources;
    }

    private SessionAccountView hydrateSession(StpLogic stpLogic, String sessionId, String accountType) {
        SaSession accountSession = stpLogic.getSessionBySessionId(sessionId);
        Object loginId = accountSession == null ? null : accountSession.getLoginId();
        if (loginId == null) {
            return null;
        }
        String accountId = String.valueOf(loginId);
        TokenBundle bundle = listTokens(stpLogic, accountId, accountType);
        List<SessionTokenView> tokens = bundle.tokens();
        SessionAccountView item = new SessionAccountView();
        item.setAccountId(accountId);
        item.setAccountType(accountType);
        item.setTokenCount(tokens.size());
        item.setTokens(tokens);
        if (!tokens.isEmpty()) {
            SessionTokenView latest = tokens.getFirst();
            item.setClientIp(latest.getClientIp());
            item.setDeviceLabel(latest.getDeviceLabel());
            item.setLatestLoginIp(latest.getClientIp());
            item.setLatestLoginTime(latest.getLoginAt());
            item.setLatestActiveAt(latest.getLastActiveAt() != null ? latest.getLastActiveAt() : latest.getLoginAt());
            item.setFirstLoginAt(tokens.stream()
                    .map(SessionTokenView::getLoginAt)
                    .filter(StringUtils::hasText)
                    .min(String::compareTo)
                    .orElse(null));
        }
        String account = bundle.account();
        item.setAccount(StringUtils.hasText(account) ? account : accountId);
        return item;
    }

    private boolean matchesFilter(SessionAccountView item, SessionPageQuery query) {
        if (StringUtils.hasText(query.accountId()) && !query.accountId().equals(item.getAccountId())) {
            return false;
        }
        if (StringUtils.hasText(query.account())) {
            String keyword = query.account().toLowerCase(Locale.ROOT);
            if (!(containsIgnoreCase(item.getAccount(), keyword)
                    || containsIgnoreCase(item.getName(), keyword)
                    || containsIgnoreCase(item.getNickname(), keyword))) {
                return false;
            }
        }
        if (StringUtils.hasText(query.ip())) {
            String ip = query.ip();
            boolean hit = item.getTokens() != null && item.getTokens().stream()
                    .anyMatch(token -> token.getClientIp() != null && token.getClientIp().contains(ip));
            if (!hit) {
                return false;
            }
        }
        if (StringUtils.hasText(query.keyword())) {
            String keyword = query.keyword().toLowerCase(Locale.ROOT);
            if (!(containsIgnoreCase(item.getAccount(), keyword)
                    || containsIgnoreCase(item.getAccountId(), keyword))) {
                return false;
            }
        }
        return true;
    }

    private AnalysisAccumulator analyzeLogic(StpLogic stpLogic) {
        AnalysisAccumulator acc = new AnalysisAccumulator();
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int offset = 0;
        while (offset < ANALYSIS_SCAN_CAP) {
            int batch = Math.min(SCAN_BATCH, ANALYSIS_SCAN_CAP - offset);
            List<String> sessionIds = stpLogic.searchSessionId("", offset, batch, false);
            if (sessionIds == null || sessionIds.isEmpty()) {
                break;
            }
            for (String sessionId : sessionIds) {
                SaSession accountSession = stpLogic.getSessionBySessionId(sessionId);
                Object loginId = accountSession == null ? null : accountSession.getLoginId();
                if (loginId == null) {
                    continue;
                }
                acc.accountCount++;
                List<String> tokenValues = stpLogic.getTokenValueListByLoginId(String.valueOf(loginId));
                int count = tokenValues == null ? 0 : tokenValues.size();
                acc.tokenCount += count;
                acc.maxToken = Math.max(acc.maxToken, count);
                if (tokenValues == null) {
                    continue;
                }
                for (String token : tokenValues) {
                    SaSession session = stpLogic.getTokenSessionByToken(token);
                    if (session == null) {
                        continue;
                    }
                    LocalDateTime loginAt = parseTime(formatEpoch(session.getCreateTime()));
                    if (loginAt != null && !loginAt.isBefore(oneHourAgo)) {
                        acc.oneHourNew++;
                    }
                }
            }
            offset += sessionIds.size();
            if (sessionIds.size() < batch) {
                break;
            }
        }
        return acc;
    }

    @SuppressWarnings("unchecked")
    private TokenBundle listTokens(StpLogic stpLogic, String accountId, String accountType) {
        List<String> tokenValues = stpLogic.getTokenValueListByLoginId(accountId);
        List<SessionTokenView> result = new ArrayList<>();
        String account = null;
        if (tokenValues == null) {
            return new TokenBundle(result, null);
        }
        for (String token : tokenValues) {
            SessionTokenView info = new SessionTokenView();
            info.setToken(token);
            info.setAccountId(accountId);
            info.setAccountType(accountType);
            SaSession session = stpLogic.getTokenSessionByToken(token);
            if (session != null) {
                info.setLoginAt(formatEpoch(session.getCreateTime()));
                Object metaObj = session.get(SessionPortImpl.LOGIN_META_KEY);
                if (metaObj instanceof Map<?, ?> meta) {
                    Object type = meta.get("accountType");
                    if (type != null && StringUtils.hasText(String.valueOf(type))) {
                        info.setAccountType(String.valueOf(type));
                    }
                    info.setClientIp(asString(meta.get("clientIp")));
                    info.setDeviceLabel(asString(meta.get("deviceLabel")));
                    info.setUserAgent(asString(meta.get("userAgent")));
                    Object remember = meta.get("rememberMe");
                    if (remember instanceof Boolean b) {
                        info.setRememberMe(Flags.of(b));
                    }
                    if (!StringUtils.hasText(account)) {
                        account = asString(meta.get("account"));
                    }
                }
            }
            long timeout = stpLogic.getTokenTimeout(token);
            if (timeout > 0) {
                info.setExpiresAt(LocalDateTime.now().plusSeconds(timeout).toString());
            }
            info.setLastActiveAt(info.getLoginAt());
            result.add(info);
        }
        result.sort((a, b) -> {
            String left = a.getLastActiveAt() != null ? a.getLastActiveAt() : "";
            String right = b.getLastActiveAt() != null ? b.getLastActiveAt() : "";
            return right.compareTo(left);
        });
        return new TokenBundle(result, account);
    }

    private StpLogic resolveLogic(String accountType) {
        if (AccountType.PORTAL.name().equalsIgnoreCase(accountType)) {
            return StpKit.PORTAL;
        }
        return StpKit.ADMIN;
    }

    private static boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String formatEpoch(long epochMillis) {
        if (epochMillis <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("Asia/Shanghai")).toString();
    }

    private static LocalDateTime parseTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private record LogicSource(StpLogic stpLogic, String accountType) {
    }

    private record TokenBundle(List<SessionTokenView> tokens, String account) {
    }

    private static final class AnalysisAccumulator {
        private int accountCount;
        private int tokenCount;
        private int oneHourNew;
        private int maxToken;
    }
}

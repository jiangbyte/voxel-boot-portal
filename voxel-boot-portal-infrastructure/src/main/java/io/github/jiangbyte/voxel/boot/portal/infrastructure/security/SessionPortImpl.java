package io.github.jiangbyte.voxel.boot.portal.infrastructure.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 StpKit 的会话端口实现。
 *
 * Author: Charlie
 */
@Component
public class SessionPortImpl implements SessionPort {

    public static final String LOGIN_META_KEY = "LOGIN_META";

    @Override
    public String loginAdmin(String accountId) {
        return login("ADMIN", accountId, accountId, null, null, null, Flags.of(true), 0);
    }

    @Override
    public String loginPortal(String accountId) {
        return login("PORTAL", accountId, accountId, null, null, null, Flags.of(true), 0);
    }

    @Override
    public String login(String accountType, String accountId, String accountLabel,
                        String clientIp, String userAgent, String deviceLabel,
                        Integer rememberMe, long ttlSeconds) {
        // 1. 选择端逻辑并登录
        StpLogic logic = resolve(accountType);
        if (ttlSeconds > 0) {
            logic.login(accountId, ttlSeconds);
        } else {
            logic.login(accountId);
        }
        // 2. 将会话元数据写入 tokenSession，供会话管理 hydrate
        SaSession tokenSession = logic.getTokenSession();
        if (tokenSession != null) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("accountId", accountId);
            meta.put("accountType", normalizeType(accountType));
            meta.put("account", StringUtils.hasText(accountLabel) ? accountLabel : accountId);
            meta.put("clientIp", clientIp);
            meta.put("userAgent", userAgent);
            meta.put("deviceLabel", deviceLabel);
            meta.put("rememberMe", rememberMe);
            tokenSession.set(LOGIN_META_KEY, meta);
        }
        // 3. 返回 token
        return logic.getTokenValue();
    }

    @Override
    public void logoutAdmin() {
        if (StpKit.ADMIN.isLogin()) {
            StpKit.ADMIN.logout();
        }
    }

    @Override
    public void logoutPortal() {
        if (StpKit.PORTAL.isLogin()) {
            StpKit.PORTAL.logout();
        }
    }

    @Override
    public void logoutAccount(String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return;
        }
        StpKit.ADMIN.logout(accountId);
        StpKit.PORTAL.logout(accountId);
    }

    @Override
    public String currentAdminAccountId() {
        if (!StpKit.ADMIN.isLogin()) {
            return null;
        }
        return String.valueOf(StpKit.ADMIN.getLoginId());
    }

    @Override
    public String currentPortalAccountId() {
        if (!StpKit.PORTAL.isLogin()) {
            return null;
        }
        return String.valueOf(StpKit.PORTAL.getLoginId());
    }

    @Override
    public String currentAccountId() {
        if (StpKit.ADMIN.isLogin()) {
            return String.valueOf(StpKit.ADMIN.getLoginId());
        }
        if (StpKit.PORTAL.isLogin()) {
            return String.valueOf(StpKit.PORTAL.getLoginId());
        }
        return null;
    }

    @Override
    public String currentAccountType() {
        if (StpKit.ADMIN.isLogin()) {
            return "ADMIN";
        }
        if (StpKit.PORTAL.isLogin()) {
            return "PORTAL";
        }
        return null;
    }

    @Override
    public RefreshSnapshot refreshCurrent(long renewTtlSeconds) {
        // 1. 定位当前端
        StpLogic logic;
        String accountType;
        if (StpKit.ADMIN.isLogin()) {
            logic = StpKit.ADMIN;
            accountType = "ADMIN";
        } else if (StpKit.PORTAL.isLogin()) {
            logic = StpKit.PORTAL;
            accountType = "PORTAL";
        } else {
            return null;
        }
        // 2. 续期
        long ttl = renewTtlSeconds;
        if (ttl <= 0) {
            ttl = logic.getTokenTimeout();
        }
        if (ttl > 0) {
            logic.renewTimeout(ttl);
        }
        // 3. 返回快照
        return new RefreshSnapshot(
                logic.getTokenValue(),
                String.valueOf(logic.getLoginId()),
                accountType,
                logic.getTokenTimeout());
    }

    private static StpLogic resolve(String accountType) {
        if ("PORTAL".equalsIgnoreCase(accountType)) {
            return StpKit.PORTAL;
        }
        return StpKit.ADMIN;
    }

    private static String normalizeType(String accountType) {
        return "PORTAL".equalsIgnoreCase(accountType) ? "PORTAL" : "ADMIN";
    }
}

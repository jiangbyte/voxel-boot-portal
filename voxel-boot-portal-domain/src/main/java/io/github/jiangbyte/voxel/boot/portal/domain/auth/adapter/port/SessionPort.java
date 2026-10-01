package io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port;

/**
 * 会话端口：由基础设施基于 Sa-Token StpKit 实现。
 *
 * Author: Charlie
 */
public interface SessionPort {

    /** 管理端登录，返回 token。 */
    String loginAdmin(String accountId);

    /** 门户端登录，返回 token。 */
    String loginPortal(String accountId);

    /**
     * 带会话元数据登录。
     *
     * @param accountType ADMIN / PORTAL
     * @param accountId 账号 ID
     * @param accountLabel 登录标识展示名
     * @param clientIp 客户端 IP
     * @param userAgent UA
     * @param deviceLabel 设备标签
     * @param rememberMe 是否记住我
     * @param ttlSeconds token TTL；&lt;=0 使用 Sa-Token 默认
     * @return token
     */
    String login(String accountType, String accountId, String accountLabel,
                 String clientIp, String userAgent, String deviceLabel,
                 Integer rememberMe, long ttlSeconds);

    /** 管理端登出。 */
    void logoutAdmin();

    /** 门户端登出。 */
    void logoutPortal();

    /**
     * 按账号 ID 强制下线（同时尝试管理端与门户）。
     */
    void logoutAccount(String accountId);

    /** 当前管理端登录账号 ID；未登录返回 null。 */
    String currentAdminAccountId();

    /** 当前门户端登录账号 ID；未登录返回 null。 */
    String currentPortalAccountId();

    /** 当前端登录账号 ID（先 admin 再 portal）；未登录返回 null。 */
    String currentAccountId();

    /** 当前端账号类型；未登录返回 null。 */
    String currentAccountType();

    /** 刷新当前 token 超时，返回当前 token 与剩余 TTL。 */
    RefreshSnapshot refreshCurrent(long renewTtlSeconds);

    /**
     * 刷新结果快照。
     */
    record RefreshSnapshot(String token, String accountId, String accountType, long expiresIn) {
    }
}

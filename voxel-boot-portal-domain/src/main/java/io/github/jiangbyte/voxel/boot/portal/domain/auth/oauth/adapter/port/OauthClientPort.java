package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

/**
 * OAuth 客户端端口：授权 URL、code 换资料、小程序登录。
 *
 * Author: Charlie
 */
public interface OauthClientPort {

    void ensureEnabled(String accountType, String provider);

    boolean isEnabled(String accountType, String provider);

    String providerLabel(String provider);

    boolean isWebOAuth(String provider);

    String buildAuthorizeUrl(String accountType, String provider, String state);

    OauthUserProfile loginByCode(String accountType, String provider, String code, String state);

    OauthUserProfile loginWechatMp(String accountType, String jsCode);
}

package io.github.jiangbyte.voxel.boot.portal.cases.auth.oauth;

/**
 * OAuth 授权跳转结果。
 *
 * Author: Charlie
 */
public class OauthAuthorizeResult {

    private String authorizeUrl;
    private String state;

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

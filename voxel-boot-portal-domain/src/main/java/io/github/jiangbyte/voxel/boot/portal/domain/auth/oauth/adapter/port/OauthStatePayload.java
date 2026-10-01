package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

/**
 * OAuth state 载荷。
 *
 * Author: Charlie
 */
public class OauthStatePayload {

    private String accountType;
    private String intent;
    private String provider;
    private String redirect;
    private String accountId;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}

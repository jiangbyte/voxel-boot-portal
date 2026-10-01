package io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port;

/**
 * 登录/刷新会话成功结果。
 *
 * Author: Charlie
 */
public class LoginResult {

    private String token;
    private String accountId;
    private String accountType;
    private Integer passwordExpired = 0;
    private Integer forceBindEmail = 0;
    private Integer forceBindPhone = 0;
    private Integer passwordExpiryWarningDays;
    private Long expiresIn;

    public LoginResult() {
    }

    public LoginResult(String token, String accountId, String accountType) {
        this.token = token;
        this.accountId = accountId;
        this.accountType = accountType;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public Integer getPasswordExpired() { return passwordExpired; }
    public void setPasswordExpired(Integer passwordExpired) { this.passwordExpired = passwordExpired; }
    public Integer getForceBindEmail() { return forceBindEmail; }
    public void setForceBindEmail(Integer forceBindEmail) { this.forceBindEmail = forceBindEmail; }
    public Integer getForceBindPhone() { return forceBindPhone; }
    public void setForceBindPhone(Integer forceBindPhone) { this.forceBindPhone = forceBindPhone; }
    public Integer getPasswordExpiryWarningDays() { return passwordExpiryWarningDays; }
    public void setPasswordExpiryWarningDays(Integer passwordExpiryWarningDays) { this.passwordExpiryWarningDays = passwordExpiryWarningDays; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}

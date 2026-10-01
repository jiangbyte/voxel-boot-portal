package io.github.jiangbyte.voxel.boot.portal.cases.auth;

/**
 * 门户注册成功结果。
 *
 * Author: Charlie
 */
public class RegisterResult {

    private String accountId;
    private String account;
    private String accountType;

    public RegisterResult() {
    }

    public RegisterResult(String accountId, String account, String accountType) {
        this.accountId = accountId;
        this.account = account;
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}

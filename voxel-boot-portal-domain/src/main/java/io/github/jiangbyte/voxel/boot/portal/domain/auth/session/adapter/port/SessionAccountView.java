package io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port;

import java.util.ArrayList;
import java.util.List;

/**
 * 账号维度在线会话视图。
 *
 * Author: Charlie
 */
public class SessionAccountView {

    private String accountId;
    private String accountType;
    private String account;
    private String name;
    private String nickname;
    private int tokenCount;
    private String clientIp;
    private String deviceLabel;
    private String latestLoginIp;
    private String latestLoginTime;
    private String latestActiveAt;
    private String firstLoginAt;
    private List<SessionTokenView> tokens = new ArrayList<>();

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    public String getLatestLoginIp() {
        return latestLoginIp;
    }

    public void setLatestLoginIp(String latestLoginIp) {
        this.latestLoginIp = latestLoginIp;
    }

    public String getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestLoginTime(String latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    public String getLatestActiveAt() {
        return latestActiveAt;
    }

    public void setLatestActiveAt(String latestActiveAt) {
        this.latestActiveAt = latestActiveAt;
    }

    public String getFirstLoginAt() {
        return firstLoginAt;
    }

    public void setFirstLoginAt(String firstLoginAt) {
        this.firstLoginAt = firstLoginAt;
    }

    public List<SessionTokenView> getTokens() {
        return tokens;
    }

    public void setTokens(List<SessionTokenView> tokens) {
        this.tokens = tokens;
    }
}

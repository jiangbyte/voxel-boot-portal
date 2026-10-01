package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

import java.time.LocalDateTime;

/**
 * OAuth 绑定快照。
 *
 * Author: Charlie
 */
public class OauthBindingInfo {

    private String id;
    private String accountId;
    private String provider;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private String rawProfile;
    private LocalDateTime boundAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRawProfile() {
        return rawProfile;
    }

    public void setRawProfile(String rawProfile) {
        this.rawProfile = rawProfile;
    }

    public LocalDateTime getBoundAt() {
        return boundAt;
    }

    public void setBoundAt(LocalDateTime boundAt) {
        this.boundAt = boundAt;
    }
}

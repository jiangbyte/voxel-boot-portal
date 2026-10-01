package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

/**
 * OAuth 用户资料（JustAuth / 小程序回包归一化）。
 *
 * Author: Charlie
 */
public class OauthUserProfile {

    private String provider;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private String rawProfileJson;

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

    public String getRawProfileJson() {
        return rawProfileJson;
    }

    public void setRawProfileJson(String rawProfileJson) {
        this.rawProfileJson = rawProfileJson;
    }
}

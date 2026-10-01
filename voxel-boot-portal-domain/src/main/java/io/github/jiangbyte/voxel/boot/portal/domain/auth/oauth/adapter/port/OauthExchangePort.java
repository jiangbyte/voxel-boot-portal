package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;

/**
 * OAuth 一次性兑换码端口：避免把 token 放入回调 URL。
 *
 * Author: Charlie
 */
public interface OauthExchangePort {

    String save(LoginResult login);

    LoginResult consume(String code);
}

package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

/**
 * OAuth state 一次性存储端口。
 *
 * Author: Charlie
 */
public interface OauthStatePort {

    String save(OauthStatePayload payload);

    OauthStatePayload consume(String state);
}

package io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port;

import java.util.List;

/**
 * OAuth 绑定仓储端口。
 *
 * Author: Charlie
 */
public interface AccountOauthPort {

    List<OauthBindingInfo> listByAccount(String accountId);

    int countByAccount(String accountId);

    OauthBindingInfo findByProviderOpenId(String provider, String openId);

    OauthBindingInfo findByWechatUnionId(String unionId);

    void upsertBinding(String accountId,
                       String provider,
                       String openId,
                       String unionId,
                       String nickname,
                       String avatar,
                       String rawProfileJson);

    void unbind(String accountId, String provider);
}

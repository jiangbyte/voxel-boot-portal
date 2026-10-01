package io.github.jiangbyte.voxel.boot.portal.cases.auth.oauth;

import io.github.jiangbyte.voxel.boot.portal.cases.auth.IAuthCase;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.AccountOauthPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthBindingInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthClientPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthExchangePort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthStatePayload;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthStatePort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthUserProfile;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IOauthCase {
    OauthAuthorizeResult authorize(AccountType accountType, String provider, String intent, String redirect);
    String handleCallback(AccountType accountType, String provider, String code, String state);
    LoginResult exchange(String code);
    LoginResult loginWechatMp(AccountType accountType, String code);
    List<OauthCaseImpl.OauthBindingResult> listCurrentBindings();
    OauthAuthorizeResult bindAuthorize(AccountType accountType, String provider);
    void unbind(String provider);
    void adminUnbind(String accountId, String provider);
}

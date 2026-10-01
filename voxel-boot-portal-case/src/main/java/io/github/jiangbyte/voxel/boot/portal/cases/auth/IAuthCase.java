package io.github.jiangbyte.voxel.boot.portal.cases.auth;

import cn.hutool.core.util.IdUtil;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.CancelAccountCommand;
import io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port.MailSenderPort;
import io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port.SmsSenderPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordApi;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.AuthCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.CaptchaResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.PasswordKeyResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IAuthCase {
    CaptchaResult captcha();
    PasswordKeyResult passwordKey();
    AuthOptionsResult authOptions(AccountType accountType);
    LoginResult adminLogin(AuthCaseImpl.LoginCommand command);
    LoginResult portalLogin(AuthCaseImpl.LoginCommand command);
    LoginResult adminLogin(String username, String password);
    LoginResult portalLogin(String username, String password);
    LoginResult issueLoginForAccount(AccountInfo account, AccountType accountType, String loginLabel);
    void sendLoginCode(AuthCaseImpl.SendCodeCommand request, AccountType accountType);
    void sendRegisterCode(AuthCaseImpl.SendCodeCommand request);
    RegisterResult registerPortal(AuthCaseImpl.RegisterCommand request);
    void forgotPassword(String email, String captchaId, String captchaValue, AccountType accountType);
    void forgotPasswordByPhone(String phone, String captchaId, String captchaValue, AccountType accountType);
    void resetPassword(String token, String passwordKeyId, String password, String captchaId, String captchaValue, AccountType accountType);
    void resetPasswordByPhone(String phone, String otpCode, String passwordKeyId, String password, String captchaId, String captchaValue, AccountType accountType);
    LoginResult refreshSession();
    void logoutAdmin();
    void logoutPortal();
    void cancelAdmin(String cancelReason);
    void cancelPortal(String cancelReason);
}

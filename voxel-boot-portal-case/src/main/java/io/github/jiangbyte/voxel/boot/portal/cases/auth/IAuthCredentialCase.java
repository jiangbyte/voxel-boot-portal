package io.github.jiangbyte.voxel.boot.portal.cases.auth;

import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountApi;
import io.github.jiangbyte.voxel.boot.portal.cases.iam.account.AccountInfo;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.admin.ProfileUserAdminApi;
import io.github.jiangbyte.voxel.boot.portal.cases.profile.portal.ProfileUserPortalApi;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.AccountType;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Flags;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.AuthCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IAuthCredentialCase {
    void sendChangePasswordCode(AccountType accountType);
    void updateCurrentPassword(AccountType accountType, String passwordKeyId, String oldPassword, String newPassword, String otpCode);
    void sendBindPhoneCode(AccountType accountType, String target);
    void sendBindEmailCode(AccountType accountType, String target);
    void updateCurrentPhone(AccountType accountType, String passwordKeyId, String password, String phone, Integer phoneLoginEnabled, String otpCode);
    void updateCurrentEmail(AccountType accountType, String passwordKeyId, String password, String email, Integer emailLoginEnabled, String otpCode);
}

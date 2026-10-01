package io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port;

import java.time.Duration;

/**
 * 认证加解密与短期凭证端口：验证码、RSA 密码密钥、重置令牌与 OTP。
 *
 * Author: Charlie
 */
public interface AuthCryptoPort {

    /** 创建图形验证码并缓存哈希。 */
    CaptchaResult createCaptcha();

    /** 校验并消费验证码（一次性）。 */
    void verifyCaptcha(String captchaId, String captchaValue);

    /** 签发 RSA 密码传输公钥。 */
    PasswordKeyResult createPasswordKey();

    /** 用私钥解密单个密文密码。 */
    String decryptPassword(String passwordKeyId, String encryptedPassword);

    /** 缓存密码重置令牌。 */
    void storeResetToken(String token, String accountId, Duration ttl);

    /** 消费重置令牌并返回账号 ID。 */
    String consumeResetToken(String token);

    /** 缓存登录 OTP。 */
    void storeLoginOtp(String accountType, String channel, String target, String code, Duration ttl);

    /** 消费登录 OTP。 */
    boolean consumeLoginOtp(String accountType, String channel, String target, String code);

    /** 缓存注册 OTP。 */
    void storeRegisterOtp(String channel, String target, String code, Duration ttl);

    /** 消费注册 OTP。 */
    boolean consumeRegisterOtp(String channel, String target, String code);

    /** 缓存手机找回密码 OTP。 */
    void storeResetPasswordOtp(String accountType, String phone, String code, Duration ttl);

    /** 消费手机找回密码 OTP。 */
    boolean consumeResetPasswordOtp(String accountType, String phone, String code);

    /** 缓存改密 OTP。 */
    void storeChangePasswordOtp(String accountType, String channel, String accountId, String code, Duration ttl);

    /** 消费改密 OTP。 */
    boolean consumeChangePasswordOtp(String accountType, String channel, String accountId, String code);

    /** 缓存绑定手机/邮箱 OTP。 */
    void storeBindOtp(String accountType, String channel, String accountId, String target, String code, Duration ttl);

    /** 消费绑定手机/邮箱 OTP。 */
    boolean consumeBindOtp(String accountType, String channel, String accountId, String target, String code);
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.auth.crypto;

import cn.hutool.core.util.IdUtil;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.AuthCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.CaptchaResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.PasswordKeyResult;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import lombok.RequiredArgsConstructor;

/**
 * {@link AuthCryptoPort} 实现：验证码 BCrypt、RSA-OAEP 密码解密、重置令牌与 OTP 的 Redis 存储。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class AuthCryptoPortImpl implements AuthCryptoPort {

    private static final String CAPTCHA_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);
    private static final Duration PASSWORD_KEY_TTL = Duration.ofMinutes(2);
    private static final String PASSWORD_KEY_SHARED = "password:crypto:shared";

    private final RedissonClient redissonClient;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();


    @Override
    public CaptchaResult createCaptcha() {
        // 1. 生成 4 位验证码并 BCrypt 后写入 Redis
        StringBuilder value = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            value.append(CAPTCHA_ALPHABET.charAt(secureRandom.nextInt(CAPTCHA_ALPHABET.length())));
        }
        String captchaId = IdUtil.simpleUUID();
        RBucket<String> bucket = redissonClient.getBucket(captchaKey(captchaId), StringCodec.INSTANCE);
        bucket.set(passwordEncoder.encode(value.toString().toLowerCase()), CAPTCHA_TTL);
        // 2. 渲染 PNG Base64
        CaptchaResult response = new CaptchaResult();
        response.setCaptchaId(captchaId);
        response.setImageType("image/png");
        response.setImageBase64(CaptchaImageRenderer.pngBase64(value.toString(), secureRandom));
        return response;
    }

    @Override
    public void verifyCaptcha(String captchaId, String captchaValue) {
        // 1. 一次性取出哈希并校验
        RBucket<String> bucket = redissonClient.getBucket(captchaKey(captchaId), StringCodec.INSTANCE);
        String hashed = bucket.getAndDelete();
        if (hashed == null || captchaValue == null
                || !passwordEncoder.matches(captchaValue.trim().toLowerCase(), hashed)) {
            throw new DomainException("验证码无效或已过期");
        }
    }

    @Override
    public PasswordKeyResult createPasswordKey() {
        try {
            // 1. 优先复用共享短时密钥
            RBucket<String> shared = redissonClient.getBucket(PASSWORD_KEY_SHARED);
            String cached = shared.get();
            if (cached != null && cached.contains("|")) {
                String[] parts = cached.split("\\|", 2);
                if (parts.length == 2 && redissonClient.getBucket(passwordKey(parts[0])).isExists()) {
                    PasswordKeyResult hit = new PasswordKeyResult();
                    hit.setKeyId(parts[0]);
                    hit.setPublicKey(parts[1]);
                    return hit;
                }
            }
            // 2. 生成 RSA-2048，私钥入 Redis，公钥返回
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            String keyId = IdUtil.simpleUUID();
            String privatePem = toPem(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            redissonClient.getBucket(passwordKey(keyId)).set(privatePem, PASSWORD_KEY_TTL);
            shared.set(keyId + "|" + publicKey, PASSWORD_KEY_TTL);
            PasswordKeyResult response = new PasswordKeyResult();
            response.setKeyId(keyId);
            response.setPublicKey(publicKey);
            return response;
        } catch (Exception ex) {
            throw new DomainException(500, "创建密码传输密钥失败");
        }
    }

    @Override
    public String decryptPassword(String passwordKeyId, String encryptedPassword) {
        // 1. 读取私钥（短时复用，不删除）
        RBucket<String> bucket = redissonClient.getBucket(passwordKey(passwordKeyId));
        String privatePem = bucket.get();
        if (privatePem == null) {
            throw new DomainException("密码加密密钥无效或已过期");
        }
        try {
            // 2. RSA-OAEP SHA-256 解密
            byte[] keyBytes = Base64.getDecoder().decode(privatePem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", ""));
            PrivateKey privateKey = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey,
                    new javax.crypto.spec.OAEPParameterSpec(
                            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
                            javax.crypto.spec.PSource.PSpecified.DEFAULT));
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (DomainException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DomainException("密码解密失败");
        }
    }

    @Override
    public void storeResetToken(String token, String accountId, Duration ttl) {
        redissonClient.getBucket(resetTokenKey(token)).set(accountId, ttl);
    }

    @Override
    public String consumeResetToken(String token) {
        return redissonClient.<String>getBucket(resetTokenKey(token)).getAndDelete();
    }

    @Override
    public void storeLoginOtp(String accountType, String channel, String target, String code, Duration ttl) {
        redissonClient.getBucket(loginOtpKey(accountType, channel, target)).set(code, ttl);
    }

    @Override
    public boolean consumeLoginOtp(String accountType, String channel, String target, String code) {
        RBucket<String> bucket = redissonClient.getBucket(loginOtpKey(accountType, channel, target));
        String stored = bucket.getAndDelete();
        return stored != null && code != null && stored.equals(code.trim());
    }

    @Override
    public void storeRegisterOtp(String channel, String target, String code, Duration ttl) {
        redissonClient.getBucket(registerOtpKey(channel, target)).set(code, ttl);
    }

    @Override
    public boolean consumeRegisterOtp(String channel, String target, String code) {
        RBucket<String> bucket = redissonClient.getBucket(registerOtpKey(channel, target));
        String stored = bucket.getAndDelete();
        return stored != null && code != null && stored.equals(code.trim());
    }

    @Override
    public void storeResetPasswordOtp(String accountType, String phone, String code, Duration ttl) {
        redissonClient.getBucket(resetPasswordOtpKey(accountType, phone)).set(code, ttl);
    }

    @Override
    public boolean consumeResetPasswordOtp(String accountType, String phone, String code) {
        RBucket<String> bucket = redissonClient.getBucket(resetPasswordOtpKey(accountType, phone));
        String stored = bucket.getAndDelete();
        return stored != null && code != null && stored.equals(code.trim());
    }

    @Override
    public void storeChangePasswordOtp(String accountType, String channel, String accountId, String code, Duration ttl) {
        redissonClient.getBucket(changePasswordOtpKey(accountType, channel, accountId)).set(code, ttl);
    }

    @Override
    public boolean consumeChangePasswordOtp(String accountType, String channel, String accountId, String code) {
        RBucket<String> bucket = redissonClient.getBucket(changePasswordOtpKey(accountType, channel, accountId));
        String stored = bucket.getAndDelete();
        return stored != null && code != null && stored.equals(code.trim());
    }

    @Override
    public void storeBindOtp(String accountType, String channel, String accountId, String target, String code, Duration ttl) {
        redissonClient.getBucket(bindOtpKey(accountType, channel, accountId, target)).set(code, ttl);
    }

    @Override
    public boolean consumeBindOtp(String accountType, String channel, String accountId, String target, String code) {
        RBucket<String> bucket = redissonClient.getBucket(bindOtpKey(accountType, channel, accountId, target));
        String stored = bucket.getAndDelete();
        return stored != null && code != null && stored.equals(code.trim());
    }

    private static String captchaKey(String captchaId) {
        return "captcha:" + captchaId;
    }

    private static String passwordKey(String keyId) {
        return "password:crypto:" + keyId;
    }

    private static String resetTokenKey(String token) {
        return "password:reset:" + token;
    }

    private static String loginOtpKey(String accountType, String channel, String target) {
        return "auth:otp:login:" + accountType + ":" + channel + ":" + target;
    }

    private static String registerOtpKey(String channel, String target) {
        return "auth:otp:register:" + channel + ":" + target;
    }

    private static String resetPasswordOtpKey(String accountType, String phone) {
        return "auth:otp:reset-password:" + accountType + ":PHONE:" + phone;
    }

    private static String changePasswordOtpKey(String accountType, String channel, String accountId) {
        return "auth:otp:change-password:" + accountType + ":" + channel + ":" + accountId;
    }

    private static String bindOtpKey(String accountType, String channel, String accountId, String target) {
        return "auth:otp:bind:" + accountType + ":" + channel + ":" + accountId + ":" + target;
    }

    private static String toPem(byte[] encoded) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(encoded);
        return "-----BEGIN PRIVATE KEY-----\n" + body + "\n-----END PRIVATE KEY-----";
    }
}

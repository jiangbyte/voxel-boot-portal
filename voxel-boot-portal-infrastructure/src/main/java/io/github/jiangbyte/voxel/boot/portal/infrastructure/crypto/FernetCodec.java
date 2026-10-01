package io.github.jiangbyte.voxel.boot.portal.infrastructure.crypto;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

/**
 * Fernet（AES-128-CBC + HMAC-SHA256）编解码器，与 Python cryptography.fernet / Gin fernet-go 兼容。
 *
 * Author: Charlie
 */
public final class FernetCodec {

    private static final byte VERSION = (byte) 0x80;
    private static final int SIGNING_KEY_LEN = 16;
    private static final int ENCRYPTION_KEY_LEN = 16;
    private static final int IV_LEN = 16;
    private static final int TIMESTAMP_LEN = 8;
    private static final int HMAC_LEN = 32;

    private final byte[] signingKey;
    private final byte[] encryptionKey;

    /**
     * 使用 URL-safe Base64 编码的 32 字节密钥构造编解码器。
     */
    public FernetCodec(String urlSafeBase64Key) {
        if (urlSafeBase64Key == null || urlSafeBase64Key.isBlank()) {
            throw new IllegalArgumentException("Fernet key is blank");
        }
        byte[] key = Base64.getUrlDecoder().decode(urlSafeBase64Key.trim());
        if (key.length != 32) {
            throw new IllegalArgumentException("Fernet key must decode to 32 bytes");
        }
        this.signingKey = Arrays.copyOfRange(key, 0, SIGNING_KEY_LEN);
        this.encryptionKey = Arrays.copyOfRange(key, SIGNING_KEY_LEN, SIGNING_KEY_LEN + ENCRYPTION_KEY_LEN);
    }

    /**
     * 粗判字符串是否像 Fernet token（以 gAAAAA 开头）。
     */
    public static boolean looksLikeToken(String value) {
        if (value == null || value.length() < 16) {
            return false;
        }
        return value.trim().startsWith("gAAAAA");
    }

    /**
     * 将明文加密为 URL-safe Base64 Fernet token。
     */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_LEN];
            new SecureRandom().nextBytes(iv);
            long ts = Instant.now().getEpochSecond();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey, "AES"), new IvParameterSpec(iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            ByteBuffer basic = ByteBuffer.allocate(1 + TIMESTAMP_LEN + IV_LEN + ciphertext.length);
            basic.put(VERSION);
            basic.putLong(ts);
            basic.put(iv);
            basic.put(ciphertext);
            byte[] basicBytes = basic.array();
            byte[] hmac = hmacSha256(basicBytes);

            ByteBuffer token = ByteBuffer.allocate(basicBytes.length + HMAC_LEN);
            token.put(basicBytes);
            token.put(hmac);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(token.array());
        } catch (Exception ex) {
            throw new IllegalStateException("Fernet encrypt failed", ex);
        }
    }

    /**
     * 尝试解密 token；对该密钥无效时返回 null。
     */
    public String tryDecrypt(String token) {
        if (!looksLikeToken(token)) {
            return null;
        }
        try {
            byte[] raw = Base64.getUrlDecoder().decode(token.trim());
            if (raw.length < 1 + TIMESTAMP_LEN + IV_LEN + HMAC_LEN) {
                return null;
            }
            if (raw[0] != VERSION) {
                return null;
            }
            byte[] basic = Arrays.copyOfRange(raw, 0, raw.length - HMAC_LEN);
            byte[] hmac = Arrays.copyOfRange(raw, raw.length - HMAC_LEN, raw.length);
            if (!MessageDigest.isEqual(hmac, hmacSha256(basic))) {
                return null;
            }
            byte[] iv = Arrays.copyOfRange(basic, 1 + TIMESTAMP_LEN, 1 + TIMESTAMP_LEN + IV_LEN);
            byte[] ciphertext = Arrays.copyOfRange(basic, 1 + TIMESTAMP_LEN + IV_LEN, basic.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey, "AES"), new IvParameterSpec(iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    private byte[] hmacSha256(byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
        return mac.doFinal(data);
    }
}

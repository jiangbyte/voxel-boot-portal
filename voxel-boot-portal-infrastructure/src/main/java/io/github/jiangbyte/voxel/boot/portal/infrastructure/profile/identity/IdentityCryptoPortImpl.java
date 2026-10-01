package io.github.jiangbyte.voxel.boot.portal.infrastructure.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.IdentityCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import lombok.RequiredArgsConstructor;

/**
 * 实名字段加解密实现：无密钥时使用 Base64 本地可逆编码，便于本地联调。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class IdentityCryptoPortImpl implements IdentityCryptoPort {

    @Value("${voxel.profile.identity.crypto-key:${APP__CONFIG_CRYPTO_KEY:local-dev-key}}")
    private final String cryptoKey;


    @Override
    public String encrypt(String plaintext) {
        if (!StringUtils.hasText(plaintext)) {
            return null;
        }
        String payload = cryptoKey + "|" + plaintext.trim();
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(String ciphertext) {
        if (!StringUtils.hasText(ciphertext)) {
            return null;
        }
        try {
            String payload = new String(Base64.getDecoder().decode(ciphertext.trim()), StandardCharsets.UTF_8);
            int idx = payload.indexOf('|');
            if (idx < 0) {
                throw new DomainException("Failed to decrypt identity field");
            }
            return payload.substring(idx + 1);
        } catch (DomainException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DomainException("Failed to decrypt identity field");
        }
    }

    @Override
    public String hashDocumentNo(String documentType, String documentNo) {
        if (!StringUtils.hasText(documentNo)) {
            return null;
        }
        String normalizedType = documentType == null ? "" : documentType.trim().toUpperCase(Locale.ROOT);
        String normalizedNo = documentNo.trim().toUpperCase(Locale.ROOT);
        return sha256Hex(normalizedType + "|" + normalizedNo);
    }

    @Override
    public String maskRealName(String realName) {
        if (!StringUtils.hasText(realName)) {
            return null;
        }
        String trimmed = realName.trim();
        if (trimmed.length() <= 1) {
            return "*";
        }
        return trimmed.charAt(0) + "*".repeat(trimmed.length() - 1);
    }

    @Override
    public String maskDocumentNo(String documentNo) {
        if (!StringUtils.hasText(documentNo)) {
            return null;
        }
        String trimmed = documentNo.trim();
        if (trimmed.length() <= 7) {
            return "*".repeat(trimmed.length());
        }
        int keepPrefix = Math.min(3, trimmed.length());
        int keepSuffix = Math.min(4, trimmed.length() - keepPrefix);
        return trimmed.substring(0, keepPrefix)
                + "*".repeat(trimmed.length() - keepPrefix - keepSuffix)
                + trimmed.substring(trimmed.length() - keepSuffix);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 hash failed", ex);
        }
    }
}

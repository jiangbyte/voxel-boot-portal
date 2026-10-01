package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.config;

import io.github.jiangbyte.voxel.boot.portal.infrastructure.crypto.FernetCodec;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 系统配置敏感值加解密：与 Gin/FastAPI 共用 Fernet 密钥（VOXEL_CONFIG_CRYPTO_KEY / APP__CONFIG_CRYPTO_KEY）。
 *
 * Author: Charlie
 */
@Component
public class ConfigCryptoService {

    private static final Logger log = LoggerFactory.getLogger(ConfigCryptoService.class);

    @Value("${voxel.config.crypto-key:${VOXEL_CONFIG_CRYPTO_KEY:${APP__CONFIG_CRYPTO_KEY:}}}")
    private String cryptoKey;

    private volatile FernetCodec codec;

    @PostConstruct
    void init() {
        // 1. 无密钥则仅警告，明文读写仍可用
        if (!StringUtils.hasText(cryptoKey)) {
            log.warn("voxel.config.crypto-key / VOXEL_CONFIG_CRYPTO_KEY is empty; config at-rest encryption disabled");
            return;
        }
        // 2. 初始化 Fernet
        try {
            codec = new FernetCodec(cryptoKey.trim());
            log.info("Config Fernet crypto enabled");
        } catch (Exception ex) {
            log.error("Invalid voxel.config.crypto-key: {}", ex.getMessage());
        }
    }

    /**
     * 读路径解密：非密文原样返回。
     */
    public String decryptForRead(String value) {
        if (!StringUtils.hasText(value) || !FernetCodec.looksLikeToken(value)) {
            return value;
        }
        if (codec == null) {
            throw new IllegalStateException(
                    "Encrypted config value present but voxel.config.crypto-key is empty");
        }
        String plain = codec.tryDecrypt(value);
        if (plain == null) {
            throw new IllegalStateException("Failed to decrypt config value with current crypto-key");
        }
        return plain;
    }

    /**
     * 写路径加密：仅敏感键且已启用编解码器时加密。
     */
    public String encryptForWrite(String value, boolean sensitive) {
        if (!sensitive || !StringUtils.hasText(value) || codec == null) {
            return value;
        }
        if (FernetCodec.looksLikeToken(value) && codec.tryDecrypt(value) != null) {
            return value;
        }
        return codec.encrypt(value);
    }

    public boolean enabled() {
        return codec != null;
    }
}

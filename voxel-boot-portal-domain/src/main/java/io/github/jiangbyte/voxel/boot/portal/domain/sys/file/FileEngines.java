package io.github.jiangbyte.voxel.boot.portal.domain.sys.file;

import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 文件存储引擎常量与 DEFAULT_FILE_ENGINE 映射。
 *
 * Author: Charlie
 */
public final class FileEngines {

    public static final String ENGINE_MINIO = "MINIO";
    public static final String ENGINE_RUSTFS = "RUSTFS";
    public static final String ENGINE_ALIYUN = "ALIYUN";
    public static final String ENGINE_TENCENT = "TENCENT";

    public static final String PROVIDER_MINIO = "minio";
    public static final String PROVIDER_RUSTFS = "rustfs";
    public static final String PROVIDER_OSS = "oss";
    public static final String PROVIDER_S3 = "s3";

    private static final Map<String, String> ENGINE_TO_PROVIDER = Map.of(
            ENGINE_MINIO, PROVIDER_MINIO,
            ENGINE_RUSTFS, PROVIDER_RUSTFS,
            ENGINE_ALIYUN, PROVIDER_OSS,
            ENGINE_TENCENT, PROVIDER_S3
    );

    private static final Map<String, String> PROVIDER_TO_ENGINE = Map.of(
            PROVIDER_MINIO, ENGINE_MINIO,
            PROVIDER_RUSTFS, ENGINE_RUSTFS,
            PROVIDER_OSS, ENGINE_ALIYUN,
            PROVIDER_S3, ENGINE_TENCENT
    );

    private static final Map<String, String> PROVIDER_TO_KEY_PREFIX = Map.of(
            PROVIDER_MINIO, "STORAGE_MINIO",
            PROVIDER_RUSTFS, "STORAGE_RUSTFS",
            PROVIDER_OSS, "STORAGE_ALIYUN",
            PROVIDER_S3, "STORAGE_TENCENT"
    );

    private static final Set<String> S3_COMPAT_PROVIDERS = Set.of(
            PROVIDER_MINIO, PROVIDER_RUSTFS, PROVIDER_OSS, PROVIDER_S3);

    private FileEngines() {
    }

    public static String normalizeEngine(String engine) {
        if (!StringUtils.hasText(engine)) {
            return null;
        }
        return engine.trim().toUpperCase(Locale.ROOT);
    }

    public static String normalizeProvider(String provider) {
        if (!StringUtils.hasText(provider)) {
            return null;
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }

    public static String engineToProvider(String engine) {
        String normalized = normalizeEngine(engine);
        if (normalized == null) {
            return null;
        }
        return ENGINE_TO_PROVIDER.get(normalized);
    }

    public static String providerToEngine(String provider) {
        String normalized = normalizeProvider(provider);
        if (normalized == null) {
            return null;
        }
        return PROVIDER_TO_ENGINE.get(normalized);
    }

    public static String resolveProvider(String engineOrProvider) {
        if (!StringUtils.hasText(engineOrProvider)) {
            return null;
        }
        String asProvider = normalizeProvider(engineOrProvider);
        if (PROVIDER_TO_ENGINE.containsKey(asProvider)) {
            return asProvider;
        }
        return engineToProvider(engineOrProvider);
    }

    public static String configKey(String provider, String fieldSuffix) {
        String prefix = PROVIDER_TO_KEY_PREFIX.get(normalizeProvider(provider));
        if (prefix == null) {
            throw new IllegalArgumentException("Unknown storage provider: " + provider);
        }
        return prefix + "_" + fieldSuffix;
    }

    public static boolean isS3Compatible(String provider) {
        return S3_COMPAT_PROVIDERS.contains(normalizeProvider(provider));
    }
}

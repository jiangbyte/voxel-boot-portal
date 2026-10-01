package io.github.jiangbyte.voxel.boot.portal.infrastructure.storage;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.config.adapter.port.ConfigApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileEngines;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ResolvedStorageSettings;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.StorageSettingsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 存储配置解析：从 ConfigApi / sys_config 组装 ResolvedStorageSettings。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class StorageSettingsPortImpl implements StorageSettingsPort {

    private final ConfigApi configApi;

    @Override
    public ResolvedStorageSettings resolve(String storageProvider) {
        // 1. 解析 provider：入参优先，否则 DEFAULT_FILE_ENGINE
        String provider = FileEngines.resolveProvider(storageProvider);
        if (provider == null) {
            String engine = configApi.getByKey(StorageSettingsPort.KEY_DEFAULT_FILE_ENGINE,
                    StorageSettingsPort.DEFAULT_ENGINE);
            provider = FileEngines.engineToProvider(engine);
        }
        if (provider == null) {
            provider = FileEngines.PROVIDER_RUSTFS;
        }
        // 2. 按 provider 组装配置
        return buildForProvider(provider);
    }

    private ResolvedStorageSettings buildForProvider(String provider) {
        // 1. 引擎与 S3 兼容性校验
        String engine = FileEngines.providerToEngine(provider);
        if (engine == null) {
            throw new DomainException(400, "Unknown storage provider: " + provider);
        }
        if (!FileEngines.isS3Compatible(provider)) {
            throw new DomainException(400, "Unsupported storage provider: " + provider);
        }
        // 2. 读取通用与 provider 专属键
        long uploadMax = parseLong(configApi.getByKey(StorageSettingsPort.KEY_UPLOAD_MAX_BYTES),
                StorageSettingsPort.DEFAULT_UPLOAD_MAX_BYTES);
        int presignExpire = (int) parseLong(configApi.getByKey(StorageSettingsPort.KEY_PRESIGN_EXPIRE_SECONDS),
                StorageSettingsPort.DEFAULT_PRESIGN_EXPIRE_SECONDS);
        boolean pathStyle = FileEngines.PROVIDER_MINIO.equals(provider)
                || FileEngines.PROVIDER_RUSTFS.equals(provider);
        String region = cfg(provider, "REGION");
        if (!StringUtils.hasText(region)) {
            region = "us-east-1";
        }
        // 3. 组装不可变配置
        return new ResolvedStorageSettings(
                provider,
                engine,
                cfg(provider, "BUCKET"),
                cfg(provider, "ENDPOINT"),
                cfg(provider, "ACCESS_KEY"),
                cfg(provider, "SECRET_KEY"),
                region,
                parseBool(cfg(provider, "USE_SSL"), true),
                pathStyle,
                cfg(provider, "BASE_URL"),
                parseBool(cfg(provider, "BUCKET_PUBLIC"), false),
                uploadMax,
                Math.max(1, presignExpire));
    }

    private String cfg(String provider, String suffix) {
        String value = configApi.getByKey(FileEngines.configKey(provider, suffix));
        return value == null ? "" : value.trim();
    }

    private static long parseLong(String raw, long defaultValue) {
        if (!StringUtils.hasText(raw)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private static boolean parseBool(String raw, boolean defaultValue) {
        if (!StringUtils.hasText(raw)) {
            return defaultValue;
        }
        String normalized = raw.trim().toLowerCase();
        if ("true".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized)) {
            return true;
        }
        if ("false".equals(normalized) || "0".equals(normalized) || "no".equals(normalized)) {
            return false;
        }
        return defaultValue;
    }
}

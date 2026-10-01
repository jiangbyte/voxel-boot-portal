package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port;

/**
 * 存储配置解析端口：按 provider / 默认引擎读取 sys_config。
 *
 * Author: Charlie
 */
public interface StorageSettingsPort {

    String KEY_DEFAULT_FILE_ENGINE = "DEFAULT_FILE_ENGINE";
    String KEY_UPLOAD_MAX_BYTES = "STORAGE_UPLOAD_MAX_BYTES";
    String KEY_PRESIGN_EXPIRE_SECONDS = "STORAGE_PRESIGN_EXPIRE_SECONDS";

    String DEFAULT_ENGINE = "RUSTFS";
    long DEFAULT_UPLOAD_MAX_BYTES = 10_485_760L;
    int DEFAULT_PRESIGN_EXPIRE_SECONDS = 3600;

    /**
     * 解析存储配置；storageProvider 为空时使用 DEFAULT_FILE_ENGINE。
     */
    ResolvedStorageSettings resolve(String storageProvider);
}

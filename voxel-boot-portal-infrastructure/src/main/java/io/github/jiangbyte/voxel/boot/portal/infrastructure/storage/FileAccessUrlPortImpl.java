package io.github.jiangbyte.voxel.boot.portal.infrastructure.storage;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileAsset;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileObjectKeys;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileAccessUrlPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ObjectStoragePort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.ResolvedStorageSettings;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.StorageSettingsPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 文件访问 URL 解析：对齐 archive FileAccessUrls + resolveAccessUrl。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class FileAccessUrlPortImpl implements FileAccessUrlPort {

    private final ObjectStoragePort objectStoragePort;
    private final StorageSettingsPort storageSettingsPort;
    private final FileRepository fileRepository;

    @Override
    public boolean isExternalUrl(String value) {
        return FileObjectKeys.isExternalUrl(value);
    }

    @Override
    public boolean looksLikePresignedUrl(String value) {
        return FileObjectKeys.looksLikePresignedUrl(value);
    }

    @Override
    public String toObjectKey(String value) {
        return FileObjectKeys.toObjectKey(value);
    }

    @Override
    public String resolveAccessUrl(String objectNameOrUrl, String storageProvider) {
        // 1. 空值直接返回
        if (!StringUtils.hasText(objectNameOrUrl)) {
            return null;
        }
        String raw = objectNameOrUrl.trim();
        // 2. 永久外链原样；预签脏 URL 抽 key 再解析
        if (isExternalUrl(raw)) {
            if (!looksLikePresignedUrl(raw)) {
                return raw;
            }
            String objectKey = toObjectKey(raw);
            if (!StringUtils.hasText(objectKey)) {
                return null;
            }
            FileAsset file = findByObjectKey(objectKey);
            String provider = file != null ? file.getStorageProvider() : storageProvider;
            ResolvedStorageSettings settings = storageSettingsPort.resolve(provider);
            return objectStoragePort.publicUrl(settings, objectKey);
        }
        // 3. 普通 object key → publicUrl
        String objectKey = toObjectKey(raw);
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        FileAsset file = findByObjectKey(objectKey);
        String provider = file != null && StringUtils.hasText(file.getStorageProvider())
                ? file.getStorageProvider()
                : storageProvider;
        ResolvedStorageSettings settings = storageSettingsPort.resolve(provider);
        return objectStoragePort.publicUrl(settings, objectKey);
    }

    private FileAsset findByObjectKey(String objectKey) {
        return fileRepository.findByObjectName(objectKey).orElseGet(() -> {
            int slash = objectKey.indexOf('/');
            if (slash > 0) {
                String alt = objectKey.substring(slash + 1);
                return fileRepository.findByObjectName(alt).orElse(null);
            }
            return null;
        });
    }
}

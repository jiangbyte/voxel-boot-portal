package io.github.jiangbyte.voxel.boot.portal.cases.sys.file;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileAsset;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileAccessUrlPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 文件跨上下文 API 实现：读时解析可访问 URL。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class FileApiProvider implements FileApi {

    private final FileRepository fileRepository;
    private final FileAccessUrlPort fileAccessUrlPort;

    @Override
    public Optional<FileInfo> getById(String id) {
        // 1. 空 ID 直接 empty
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        // 2. 仓储加载并投影（含解析 URL）
        return fileRepository.findById(id).map(this::toInfo);
    }

    @Override
    public List<FileInfo> listByIds(Collection<String> ids) {
        // 1. 空集合直接返回
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        // 2. 去空白后批量查询
        List<String> normalized = new ArrayList<>();
        for (String id : ids) {
            if (StringUtils.hasText(id)) {
                normalized.add(id);
            }
        }
        if (normalized.isEmpty()) {
            return List.of();
        }
        return fileRepository.findByIds(normalized).stream().map(this::toInfo).toList();
    }

    @Override
    public Optional<FileInfo> getByObjectName(String objectName) {
        // 1. 空键直接 empty
        if (!StringUtils.hasText(objectName)) {
            return Optional.empty();
        }
        // 2. 按 objectName 查询
        return fileRepository.findByObjectName(objectName).map(this::toInfo);
    }

    private FileInfo toInfo(FileAsset file) {
        // 1. 读时解析访问 URL
        String resolved = fileAccessUrlPort.resolveAccessUrl(file.getObjectName(), file.getStorageProvider());
        String url = StringUtils.hasText(resolved) ? resolved : file.getUrl();
        return new FileInfo(
                file.getId(),
                file.getObjectName(),
                file.getOriginalName(),
                file.getContentType(),
                file.getSize(),
                url);
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.file;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileAccessUrlPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FilePageQuery;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 文件只读查询实现（读时 withResolvedUrl）。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class FileQueryServiceImpl implements FileQueryService {

    private final FileMapper fileMapper;
    private final FileAccessUrlPort fileAccessUrlPort;

    @Override
    public FileView detail(String id) {
        // 1. 按主键加载
        FilePO po = fileMapper.selectById(id);
        // 2. 不存在则 404
        if (po == null) {
            throw new DomainException(404, "File not found");
        }
        // 3. 转为视图并解析访问 URL
        return withResolvedUrl(toView(po));
    }

    @Override
    public List<FileView> listByIds(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 2. 批量查询并解析 URL
        return fileMapper.selectBatchIds(ids).stream().map(this::toView).map(this::withResolvedUrl).toList();
    }

    @Override
    public FileView findByObjectName(String objectName) {
        // 1. 空键直接返回 null
        if (!StringUtils.hasText(objectName)) {
            return null;
        }
        // 2. 按 objectName 精确查一条
        FilePO po = fileMapper.selectOne(Wrappers.<FilePO>lambdaQuery()
                .eq(FilePO::getObjectName, objectName)
                .last("LIMIT 1"));
        return po == null ? null : withResolvedUrl(toView(po));
    }

    @Override
    public PageResult<FileView> page(FilePageQuery query) {
        // 1. 数据库分页查询
        Page<FilePO> page = fileMapper.selectPage(
                new Page<>(query.current(), query.size()),
                Wrappers.<FilePO>lambdaQuery()
                        .like(StringUtils.hasText(query.originalName()), FilePO::getOriginalName, query.originalName())
                        .eq(StringUtils.hasText(query.storageProvider()), FilePO::getStorageProvider, query.storageProvider())
                        .like(StringUtils.hasText(query.objectName()), FilePO::getObjectName, query.objectName())
                        .orderByDesc(FilePO::getCreatedAt));
        // 2. 映射为应用层 PageResult 并解析 URL
        List<FileView> records = page.getRecords().stream()
                .map(this::toView)
                .map(this::withResolvedUrl)
                .toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private FileView withResolvedUrl(FileView view) {
        if (view == null || !StringUtils.hasText(view.objectName())) {
            return view;
        }
        String resolved = fileAccessUrlPort.resolveAccessUrl(view.objectName(), view.storageProvider());
        if (!StringUtils.hasText(resolved)) {
            return view;
        }
        return new FileView(
                view.id(),
                view.objectName(),
                view.originalName(),
                view.storageProvider(),
                view.bucket(),
                view.contentType(),
                view.size(),
                resolved,
                view.createdBy(),
                view.createdAt(),
                view.updatedAt());
    }

    private FileView toView(FilePO po) {
        return new FileView(
                po.getId(),
                po.getObjectName(),
                po.getOriginalName(),
                po.getStorageProvider(),
                po.getBucket(),
                po.getContentType(),
                po.getSize(),
                po.getUrl(),
                po.getCreatedBy(),
                po.getCreatedAt(),
                po.getUpdatedAt());
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainEventPublisher;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileAsset;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.repository.FileRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.file.FileAssembler;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.file.FileMapper;
import io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.file.FilePO;
import lombok.RequiredArgsConstructor;

/**
 * 文件仓储实现：PO 映射与领域事件派发。
 *
 * Author: Charlie
 */
@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private final FileMapper fileMapper;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Optional<FileAsset> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(FileAssembler.toAggregate(fileMapper.selectById(id)));
    }

    @Override
    public Optional<FileAsset> findByObjectName(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            return Optional.empty();
        }
        FilePO po = fileMapper.selectOne(Wrappers.<FilePO>lambdaQuery()
                .eq(FilePO::getObjectName, objectName)
                .last("LIMIT 1"));
        return Optional.ofNullable(FileAssembler.toAggregate(po));
    }

    @Override
    public List<FileAsset> findByIds(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 2. IN 批量查询并转聚合
        return fileMapper.selectBatchIds(ids).stream()
                .map(FileAssembler::toAggregate)
                .toList();
    }

    @Override
    public void save(FileAsset aggregate) {
        // 1. 聚合转 PO
        FilePO po = FileAssembler.INSTANCE.toPO(aggregate);
        // 2. 按主键判断插入或更新
        FilePO existing = fileMapper.selectById(po.getId());
        if (existing == null) {
            fileMapper.insert(po);
        } else {
            fileMapper.updateById(po);
        }
        // 3. 派发领域事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        fileMapper.deleteById(id);
    }

    @Override
    public void remove(FileAsset aggregate) {
        // 1. 删除持久化记录
        if (aggregate == null || !StringUtils.hasText(aggregate.getId())) {
            return;
        }
        fileMapper.deleteById(aggregate.getId());
        // 2. 派发删除等已登记事件
        domainEventPublisher.publishAll(aggregate.pullDomainEvents());
    }
}

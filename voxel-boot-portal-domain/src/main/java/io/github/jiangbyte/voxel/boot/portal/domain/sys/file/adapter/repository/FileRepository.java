package io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.repository;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.adapter.repository.Repository;

import java.util.List;
import java.util.Optional;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileAsset;

/**
 * 文件元数据聚合仓储端口。
 *
 * Author: Charlie
 */
public interface FileRepository extends Repository<FileAsset, String> {

    /**
     * 按 objectName 精确查找。
     */
    Optional<FileAsset> findByObjectName(String objectName);

    /**
     * 按主键批量查询。
     */
    List<FileAsset> findByIds(List<String> ids);

    /**
     * 删除聚合并派发其已登记的领域事件。
     */
    void remove(FileAsset aggregate);
}

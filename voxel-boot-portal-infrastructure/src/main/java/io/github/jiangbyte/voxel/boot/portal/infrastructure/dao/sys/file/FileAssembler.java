package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.file;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.FileAsset;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 文件聚合与持久化对象互转。
 */
@Mapper
public interface FileAssembler {

    FileAssembler INSTANCE = Mappers.getMapper(FileAssembler.class);

    FilePO toPO(FileAsset file);


    /**
     * 持久化对象转聚合。
     */
    public static FileAsset toAggregate(FilePO po) {
        if (po == null) {
            return null;
        }
        return FileAsset.reconstitute(
                po.getId(),
                po.getObjectName(),
                po.getOriginalName(),
                po.getStorageProvider(),
                po.getBucket(),
                po.getContentType(),
                po.getSize(),
                po.getUrl());
    }
}

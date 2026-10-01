package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.Dict;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.DictCategory;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.DictStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 字典聚合与持久化对象互转。
 */
@Mapper
public interface DictAssembler {

    DictAssembler INSTANCE = Mappers.getMapper(DictAssembler.class);

    @Mapping(target = "category", expression = "java(io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.DictCategory.toValue(dict.getCategory()))")
    @Mapping(target = "status", expression = "java(dict.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.DictStatus.ENABLED.name() : dict.getStatus().name())")
    DictPO toPO(Dict dict);


    /**
     * 持久化对象转聚合。
     */
    public static Dict toAggregate(DictPO po) {
        if (po == null) {
            return null;
        }
        return Dict.reconstitute(
                po.getId(),
                po.getCode(),
                po.getLabel(),
                po.getValue(),
                po.getColor(),
                po.getCategory(),
                po.getParentId(),
                po.getStatus(),
                po.getSort());
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.position;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.Position;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.position.PositionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 岗位聚合与持久化对象互转。
 */
@Mapper
public interface PositionAssembler {

    PositionAssembler INSTANCE = Mappers.getMapper(PositionAssembler.class);

    @Mapping(source = "virtual", target = "isVirtual")
    @Mapping(target = "status", expression = "java(position.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.position.PositionStatus.ENABLED.name() : position.getStatus().name())")
    @Mapping(target = "extra", expression = "java(position.getExtra() == null ? \"{}\" : position.getExtra())")
    PositionPO toPO(Position position);


    /**
     * 持久化对象转聚合。
     */
    public static Position toAggregate(PositionPO po) {
        if (po == null) {
            return null;
        }
        return Position.reconstitute(
                po.getId(),
                po.getName(),
                po.getCategory(),
                po.getOwnerDeptId(),
                po.getSort(),
                po.getIsVirtual(),
                po.getStatus(),
                po.getDescription(),
                po.getExtra());
    }
}

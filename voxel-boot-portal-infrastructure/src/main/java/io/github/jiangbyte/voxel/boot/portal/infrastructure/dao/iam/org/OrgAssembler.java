package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.org;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.Org;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.org.OrgStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 组织聚合与持久化对象互转。
 */
@Mapper
public interface OrgAssembler {

    OrgAssembler INSTANCE = Mappers.getMapper(OrgAssembler.class);

    @Mapping(source = "virtual", target = "isVirtual")
    @Mapping(target = "status", expression = "java(dept.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.org.OrgStatus.ENABLED.name() : dept.getStatus().name())")
    @Mapping(target = "extra", expression = "java(dept.getExtra() == null ? \"{}\" : dept.getExtra())")
    OrgPO toPO(Org dept);


    /**
     * 持久化对象转聚合。
     */
    public static Org toAggregate(OrgPO po) {
        if (po == null) {
            return null;
        }
        return Org.reconstitute(
                po.getId(),
                po.getParentId(),
                po.getMasterId(),
                po.getDeputyMasterId(),
                po.getName(),
                po.getCategory(),
                po.getSort(),
                po.getIsVirtual(),
                po.getStatus(),
                po.getExtra());
    }
}

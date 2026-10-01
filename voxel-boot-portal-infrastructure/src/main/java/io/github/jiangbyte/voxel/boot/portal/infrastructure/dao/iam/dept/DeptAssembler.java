package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.dept;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.Dept;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.DeptStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 部门聚合与持久化对象互转。
 */
@Mapper
public interface DeptAssembler {

    DeptAssembler INSTANCE = Mappers.getMapper(DeptAssembler.class);

    @Mapping(source = "virtual", target = "isVirtual")
    @Mapping(target = "status", expression = "java(dept.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.dept.DeptStatus.ENABLED.name() : dept.getStatus().name())")
    @Mapping(target = "extra", expression = "java(dept.getExtra() == null ? \"{}\" : dept.getExtra())")
    DeptPO toPO(Dept dept);


    /**
     * 持久化对象转聚合。
     */
    public static Dept toAggregate(DeptPO po) {
        if (po == null) {
            return null;
        }
        return Dept.reconstitute(
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

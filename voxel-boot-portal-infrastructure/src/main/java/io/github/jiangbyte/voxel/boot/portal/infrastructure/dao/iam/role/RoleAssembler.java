package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.role;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.Role;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.role.RoleStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 角色聚合与持久化对象互转。
 */
@Mapper
public interface RoleAssembler {

    RoleAssembler INSTANCE = Mappers.getMapper(RoleAssembler.class);

    @Mapping(target = "status", expression = "java(role.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.role.RoleStatus.ENABLED.name() : role.getStatus().name())")
    @Mapping(source = "builtin", target = "isBuiltin")
    @Mapping(target = "extra", expression = "java(\"{}\")")
    RolePO toPO(Role role);


    /**
     * 持久化对象转聚合。
     */
    public static Role toAggregate(RolePO po) {
        if (po == null) {
            return null;
        }
        return Role.reconstitute(
                po.getId(),
                po.getCode(),
                po.getName(),
                po.getCategory(),
                po.getScopeType(),
                po.getOwnerDeptId(),
                po.getSort(),
                po.getStatus(),
                po.getIsBuiltin(),
                po.getDescription());
    }
}

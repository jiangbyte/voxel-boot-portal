package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.iam.group;

import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.Group;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.group.GroupStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 用户组聚合与持久化对象互转。
 */
@Mapper
public interface GroupAssembler {

    GroupAssembler INSTANCE = Mappers.getMapper(GroupAssembler.class);

    @Mapping(target = "status", expression = "java(group.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.iam.group.GroupStatus.ENABLED.name() : group.getStatus().name())")
    @Mapping(target = "extra", expression = "java(group.getExtra() == null ? \"{}\" : group.getExtra())")
    GroupPO toPO(Group group);


    /**
     * 持久化对象转聚合。
     */
    public static Group toAggregate(GroupPO po) {
        if (po == null) {
            return null;
        }
        return Group.reconstitute(
                po.getId(),
                po.getName(),
                po.getOwnerDeptId(),
                po.getDescription(),
                po.getStatus(),
                po.getExtra());
    }
}

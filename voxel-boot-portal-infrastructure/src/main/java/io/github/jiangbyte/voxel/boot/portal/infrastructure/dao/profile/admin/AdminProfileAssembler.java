package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.admin;

import io.github.jiangbyte.voxel.boot.portal.domain.profile.admin.AdminProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 管理端资料聚合与持久化对象互转。
 */
@Mapper
public interface AdminProfileAssembler {

    AdminProfileAssembler INSTANCE = Mappers.getMapper(AdminProfileAssembler.class);

    AdminProfilePO toPO(AdminProfile profile);


    /**
     * 持久化对象转聚合。
     */
    public static AdminProfile toAggregate(AdminProfilePO po) {
        if (po == null) {
            return null;
        }
        return AdminProfile.reconstitute(
                po.getAccountId(),
                po.getNickname(),
                po.getAvatar(),
                po.getSignature(),
                po.getPhone(),
                po.getEmail(),
                po.getRemark());
    }
}

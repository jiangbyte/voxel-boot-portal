package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.profile.portal;

import io.github.jiangbyte.voxel.boot.portal.domain.profile.portal.PortalProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 门户资料聚合与 PO 转换。
 */
@Mapper
public interface PortalProfileAssembler {

    PortalProfileAssembler INSTANCE = Mappers.getMapper(PortalProfileAssembler.class);

    PortalProfilePO toPO(PortalProfile profile);


    public static PortalProfile toAggregate(PortalProfilePO po) {
        if (po == null) {
            return null;
        }
        return PortalProfile.reconstitute(
                po.getAccountId(), po.getNickname(), po.getAvatar(),
                po.getSignature(), po.getPhone(), po.getEmail());
    }
}

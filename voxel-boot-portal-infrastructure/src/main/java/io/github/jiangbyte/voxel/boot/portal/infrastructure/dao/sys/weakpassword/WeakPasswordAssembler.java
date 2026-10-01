package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.weakpassword;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.WeakPassword;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 弱密码聚合与持久化对象互转。
 */
@Mapper
public interface WeakPasswordAssembler {

    WeakPasswordAssembler INSTANCE = Mappers.getMapper(WeakPasswordAssembler.class);

    WeakPasswordPO toPO(WeakPassword aggregate);


    /**
     * 持久化对象转聚合。
     */
    public static WeakPassword toAggregate(WeakPasswordPO po) {
        if (po == null) {
            return null;
        }
        return WeakPassword.reconstitute(po.getId(), po.getPassword());
    }
}

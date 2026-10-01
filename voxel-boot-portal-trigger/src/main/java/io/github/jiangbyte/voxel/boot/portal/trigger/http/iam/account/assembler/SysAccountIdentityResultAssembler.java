package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.account.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.result.SysAccountIdentityResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.AccountIdentityView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.ArrayList;
import java.util.List;

/**
 * 账号身份 View → Result 装配。
 */
@Mapper
public interface SysAccountIdentityResultAssembler {

    SysAccountIdentityResultAssembler INSTANCE = Mappers.getMapper(SysAccountIdentityResultAssembler.class);

    SysAccountIdentityResult toResult(AccountIdentityView view);


    List<SysAccountIdentityResult> toResultList(List<AccountIdentityView> views);
}

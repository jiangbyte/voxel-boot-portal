package io.github.jiangbyte.voxel.boot.portal.trigger.http.iam.account.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.iam.web.account.result.SysAccountResult;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.adapter.port.AccountView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 AccountView 装配为 API 响应 SysAccountResult。
 */
@Mapper(uses = SysAccountIdentityResultAssembler.class)
public interface SysAccountResultAssembler {

    SysAccountResultAssembler INSTANCE = Mappers.getMapper(SysAccountResultAssembler.class);

    SysAccountResult toResult(AccountView view);
}

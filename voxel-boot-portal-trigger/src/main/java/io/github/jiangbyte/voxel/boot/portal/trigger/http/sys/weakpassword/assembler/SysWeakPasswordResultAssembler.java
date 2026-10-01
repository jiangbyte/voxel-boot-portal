package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.weakpassword.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.weakpassword.result.SysWeakPasswordResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.port.WeakPasswordView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 WeakPasswordView 装配为 API 响应 SysWeakPasswordResult。
 */
@Mapper
public interface SysWeakPasswordResultAssembler {

    SysWeakPasswordResultAssembler INSTANCE = Mappers.getMapper(SysWeakPasswordResultAssembler.class);

    SysWeakPasswordResult toResult(WeakPasswordView view);
}

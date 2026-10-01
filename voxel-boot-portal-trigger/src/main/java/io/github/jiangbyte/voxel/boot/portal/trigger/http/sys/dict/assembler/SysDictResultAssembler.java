package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.dict.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.dict.result.SysDictResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.dict.adapter.port.DictView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 DictView 装配为 API 响应 SysDictResult。
 */
@Mapper
public interface SysDictResultAssembler {

    SysDictResultAssembler INSTANCE = Mappers.getMapper(SysDictResultAssembler.class);

    SysDictResult toResult(DictView view);
}

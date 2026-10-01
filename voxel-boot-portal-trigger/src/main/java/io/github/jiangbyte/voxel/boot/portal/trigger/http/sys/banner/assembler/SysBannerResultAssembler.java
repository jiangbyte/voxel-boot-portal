package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.banner.assembler;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.banner.result.SysBannerResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将应用层 BannerView 装配为 API 响应 SysBannerResult。
 */
@Mapper
public interface SysBannerResultAssembler {

    SysBannerResultAssembler INSTANCE = Mappers.getMapper(SysBannerResultAssembler.class);

    SysBannerResult toResult(BannerView view);
}

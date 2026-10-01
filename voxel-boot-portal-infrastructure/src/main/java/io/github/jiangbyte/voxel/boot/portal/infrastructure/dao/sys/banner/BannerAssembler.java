package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.BannerStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Banner 聚合与持久化对象互转。
 */
@Mapper
public interface BannerAssembler {

    BannerAssembler INSTANCE = Mappers.getMapper(BannerAssembler.class);

    @Mapping(target = "targetAccountTypes", expression = "java(banner.getTargetAccountTypes() == null ? \"[]\" : banner.getTargetAccountTypes())")
    @Mapping(target = "interactionCount", expression = "java(banner.getInteractionCount() == null ? 0L : banner.getInteractionCount())")
    @Mapping(target = "status", expression = "java(banner.getStatus() == null ? io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.BannerStatus.ENABLED.name() : banner.getStatus().name())")
    BannerPO toPO(Banner banner);


    /**
     * 持久化对象转聚合。
     */
    public static Banner toAggregate(BannerPO po) {
        if (po == null) {
            return null;
        }
        return Banner.reconstitute(
                po.getId(),
                po.getTitle(),
                po.getImage(),
                po.getUrl(),
                po.getLinkType(),
                po.getSummary(),
                po.getDescription(),
                po.getCategory(),
                po.getType(),
                po.getPosition(),
                po.getTargetAccountTypes(),
                po.getSort(),
                po.getInteractionCount(),
                po.getStatus(),
                po.getStartAt(),
                po.getEndAt());
    }
}

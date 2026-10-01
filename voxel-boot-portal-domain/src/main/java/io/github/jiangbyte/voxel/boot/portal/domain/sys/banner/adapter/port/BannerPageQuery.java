package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 分页查询。
 *
 * Author: Charlie
 */
public record BannerPageQuery(
        long current,
        long size,
        String targetAccountType,
        String category,
        String type,
        String position,
        String status
) {
}

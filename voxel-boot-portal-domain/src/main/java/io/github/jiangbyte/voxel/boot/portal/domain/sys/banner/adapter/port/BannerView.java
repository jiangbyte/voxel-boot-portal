package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port;

import java.time.LocalDateTime;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 只读视图。
 *
 * Author: Charlie
 */
public record BannerView(
        String id,
        String title,
        String image,
        String imageUrl,
        String url,
        String linkType,
        String summary,
        String description,
        String category,
        String type,
        String position,
        List<String> targetAccountTypes,
        Integer sort,
        Long interactionCount,
        String status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

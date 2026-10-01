package io.github.jiangbyte.voxel.boot.portal.cases.sys.banner;

import java.time.LocalDateTime;
import java.util.List;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * 创建 Banner 命令。
 *
 * Author: Charlie
 */
public record CreateBannerCommand(
        String title,
        String image,
        String url,
        String linkType,
        String summary,
        String description,
        String category,
        String type,
        String position,
        List<String> targetAccountTypes,
        Integer sort,
        String status,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}

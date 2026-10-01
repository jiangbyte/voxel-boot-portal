package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 链接安全校验端口（由基础设施实现）。
 *
 * Author: Charlie
 */
public interface BannerLinkGuard {

    /**
     * 校验 linkType 与 url 组合是否安全。
     */
    void validate(String linkType, String url);
}

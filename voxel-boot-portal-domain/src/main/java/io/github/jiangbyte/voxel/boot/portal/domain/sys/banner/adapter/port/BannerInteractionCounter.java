package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;

/**
 * Banner 互动计数端口：优先 Redis 增量，不可用时回落 DB。
 *
 * Author: Charlie
 */
public interface BannerInteractionCounter {

    /**
     * 互动计数 +1。
     */
    void increment(String bannerId);
}

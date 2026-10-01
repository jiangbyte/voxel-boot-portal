package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.banner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.adapter.port.BannerInteractionCounter;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.banner.Banner;
import lombok.RequiredArgsConstructor;

/**
 * Banner 互动计数：优先 Redis HINCRBY，否则直接累加 DB interaction_count。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class BannerInteractionCounterImpl implements BannerInteractionCounter {

    public static final String INTERACTION_DELTA_KEY = "voxel:banner:interaction:deltas";

    private final BannerMapper bannerMapper;
    private final ObjectProvider<RedissonClient> redissonClientProvider;


    @Override
    public void increment(String bannerId) {
        // 1. 校验 bannerId
        if (!StringUtils.hasText(bannerId)) {
            return;
        }
        // 2. Redisson 可用则写入增量哈希，否则直接 UPDATE DB
        RedissonClient redissonClient = redissonClientProvider.getIfAvailable();
        if (redissonClient != null) {
            RMap<String, Long> deltas = redissonClient.getMap(INTERACTION_DELTA_KEY);
            deltas.addAndGet(bannerId, 1L);
            return;
        }
        bannerMapper.update(null, Wrappers.<BannerPO>lambdaUpdate()
                .eq(BannerPO::getId, bannerId)
                .setSql("interaction_count = COALESCE(interaction_count, 0) + 1"));
    }
}

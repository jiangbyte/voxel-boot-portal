package io.github.jiangbyte.voxel.boot.portal.infrastructure.auth.oauth;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthStatePayload;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthStatePort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import lombok.RequiredArgsConstructor;

/**
 * OAuth state 一次性 Redis 存储。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class OauthStatePortImpl implements OauthStatePort {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;


    @Override
    public String save(OauthStatePayload payload) {
        String state = IdUtil.simpleUUID();
        try {
            redissonClient.getBucket(key(state)).set(objectMapper.writeValueAsString(payload), TTL);
        } catch (Exception e) {
            throw new DomainException("无法创建 OAuth state");
        }
        return state;
    }

    @Override
    public OauthStatePayload consume(String state) {
        if (!StringUtils.hasText(state)) {
            return null;
        }
        RBucket<String> bucket = redissonClient.getBucket(key(state.trim()));
        String json = bucket.getAndDelete();
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, OauthStatePayload.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static String key(String state) {
        return "oauth:state:" + state;
    }
}

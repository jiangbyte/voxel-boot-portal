package io.github.jiangbyte.voxel.boot.portal.infrastructure.auth.oauth;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.LoginResult;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.oauth.adapter.port.OauthExchangePort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import lombok.RequiredArgsConstructor;

/**
 * OAuth 登录一次性兑换码存储。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class OauthExchangePortImpl implements OauthExchangePort {

    private static final Duration TTL = Duration.ofMinutes(2);

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;


    @Override
    public String save(LoginResult login) {
        if (login == null || !StringUtils.hasText(login.getToken())) {
            throw new DomainException("登录结果无效");
        }
        String code = IdUtil.simpleUUID();
        try {
            redissonClient.getBucket(key(code)).set(objectMapper.writeValueAsString(login), TTL);
        } catch (Exception ex) {
            throw new DomainException(500, "保存 OAuth 兑换码失败");
        }
        return code;
    }

    @Override
    public LoginResult consume(String code) {
        if (!StringUtils.hasText(code)) {
            throw new DomainException("兑换码无效或已过期");
        }
        RBucket<String> bucket = redissonClient.getBucket(key(code.trim()));
        String json = bucket.getAndDelete();
        if (!StringUtils.hasText(json)) {
            throw new DomainException("兑换码无效或已过期");
        }
        try {
            return objectMapper.readValue(json, LoginResult.class);
        } catch (Exception ex) {
            throw new DomainException("兑换码无效或已过期");
        }
    }

    private static String key(String code) {
        return "oauth:exchange:" + code;
    }
}

package io.github.jiangbyte.voxel.boot.portal.infrastructure.notify;

import io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port.SmsSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 本地短信适配器：仅记录日志，不真实外发。
 *
 * Author: Charlie
 */
@Component
public class LoggingSmsSenderAdapter implements SmsSenderPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingSmsSenderAdapter.class);

    @Override
    public void sendTemplated(String scene, String to, Map<String, Object> vars) {
        // 1. 脱敏手机号后打日志
        log.info("[sms-noop] scene={} to={} vars={}", scene, mask(to), vars);
    }

    private static String mask(String to) {
        if (to == null || to.length() < 7) {
            return "***";
        }
        return to.substring(0, 3) + "****" + to.substring(to.length() - 4);
    }
}

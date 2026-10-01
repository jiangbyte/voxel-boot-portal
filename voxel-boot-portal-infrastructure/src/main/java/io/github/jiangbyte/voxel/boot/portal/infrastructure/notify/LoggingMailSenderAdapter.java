package io.github.jiangbyte.voxel.boot.portal.infrastructure.notify;

import io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port.MailSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 本地邮件适配器：仅记录日志，不真实外发。
 *
 * Author: Charlie
 */
@Component
public class LoggingMailSenderAdapter implements MailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingMailSenderAdapter.class);

    @Override
    public void sendTemplated(String scene, String to, Map<String, Object> vars) {
        // 1. 脱敏收件人后打日志，便于本地联调 OTP
        log.info("[mail-noop] scene={} to={} vars={}", scene, mask(to), vars);
    }

    private static String mask(String to) {
        if (to == null || to.length() < 3) {
            return "***";
        }
        int at = to.indexOf('@');
        if (at > 1) {
            return to.charAt(0) + "***" + to.substring(at);
        }
        return to.substring(0, 1) + "***";
    }
}

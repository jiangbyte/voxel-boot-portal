package io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port;

import java.util.Map;

/**
 * 邮件发送端口：按场景模板发送，本地可用日志适配器。
 *
 * Author: Charlie
 */
public interface MailSenderPort {

    /**
     * 按场景发送模板邮件。
     *
     * @param scene 场景码，如 LOGIN_CODE / RESET_PASSWORD_CODE
     * @param to    收件人
     * @param vars  模板变量
     */
    void sendTemplated(String scene, String to, Map<String, Object> vars);
}

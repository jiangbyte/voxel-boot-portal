package io.github.jiangbyte.voxel.boot.portal.domain.notify.adapter.port;

import java.util.Map;

/**
 * 短信发送端口：按场景模板发送，本地可用日志适配器。
 *
 * Author: Charlie
 */
public interface SmsSenderPort {

    /**
     * 按场景发送模板短信。
     *
     * @param scene 场景码
     * @param to    手机号
     * @param vars  模板变量
     */
    void sendTemplated(String scene, String to, Map<String, Object> vars);
}

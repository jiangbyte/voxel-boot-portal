package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port;

/**
 * 第三方实名核验端口。
 *
 * Author: Charlie
 */
public interface RealNameVerifyPort {

    /** 是否支持该通道/证件/提供方。 */
    boolean supports(String verifyChannel, String documentType, String provider);

    /** 初始化核验，返回 provider/订单号/跳转地址。 */
    RealNameVerifyInitResult initVerify(String caseId, String documentType, String realName, String documentNo, String provider);

    /** 处理回调前校验（Mock 可空实现）。 */
    void handleCallback(String caseId, String providerOrderNo, Integer success, String message);
}

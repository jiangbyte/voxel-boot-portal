package io.github.jiangbyte.voxel.boot.portal.infrastructure.profile.identity;

import cn.hutool.core.util.IdUtil;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.RealNameVerifyInitResult;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.RealNameVerifyPort;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.VerifyChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Mock 第三方实名核验：本地默认自动通过，可通过配置强制失败。
 *
 * Author: Charlie
 */
@Component
@RequiredArgsConstructor
public class MockRealNameVerifyAdapter implements RealNameVerifyPort {

    @Value("${voxel.profile.identity.mock.auto-pass:true}")
    private final boolean autoPass;


    @Override
    public boolean supports(String verifyChannel, String documentType, String provider) {
        if (!VerifyChannel.THIRD_PARTY.name().equalsIgnoreCase(verifyChannel)) {
            return false;
        }
        return provider == null || provider.isBlank() || "MOCK".equalsIgnoreCase(provider);
    }

    @Override
    public RealNameVerifyInitResult initVerify(String caseId,
                                               String documentType,
                                               String realName,
                                               String documentNo,
                                               String provider) {
        String orderNo = "MOCK-" + IdUtil.simpleUUID();
        return new RealNameVerifyInitResult(
                caseId,
                "MOCK",
                orderNo,
                "/mock/identity-verify?caseId=" + caseId + "&autoPass=" + autoPass);
    }

    @Override
    public void handleCallback(String caseId, String providerOrderNo, Integer success, String message) {
        // Mock 无额外副作用；结果由用例层根据 success 流转
    }

    public boolean isAutoPass() {
        return autoPass;
    }
}

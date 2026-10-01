package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port;

/**
 * 第三方核验初始化结果。
 *
 * Author: Charlie
 */
public record RealNameVerifyInitResult(
        String caseId,
        String provider,
        String providerOrderNo,
        String redirectUrl
) {
}

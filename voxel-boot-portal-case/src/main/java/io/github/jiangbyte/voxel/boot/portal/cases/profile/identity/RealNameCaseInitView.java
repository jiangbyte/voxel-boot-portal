package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

/**
 * 三方初始化结果视图。
 *
 * Author: Charlie
 */
public record RealNameCaseInitView(
        String caseId,
        String provider,
        String providerOrderNo,
        String redirectUrl
) {
}

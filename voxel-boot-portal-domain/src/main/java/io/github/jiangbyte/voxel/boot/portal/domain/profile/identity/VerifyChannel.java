package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

/**
 * 实名认证通道。
 *
 * Author: Charlie
 */
public enum VerifyChannel {
    MANUAL,
    THIRD_PARTY;

    public static VerifyChannel from(String value) {
        if (value == null || value.isBlank()) {
            return MANUAL;
        }
        return VerifyChannel.valueOf(value.trim().toUpperCase());
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

/**
 * 实名业务类型。
 *
 * Author: Charlie
 */
public enum RealNameBusinessType {
    ACCOUNT_VERIFY,
    ACCOUNT_RECOVERY;

    public static RealNameBusinessType from(String value) {
        if (value == null || value.isBlank()) {
            return ACCOUNT_VERIFY;
        }
        return RealNameBusinessType.valueOf(value.trim().toUpperCase());
    }
}

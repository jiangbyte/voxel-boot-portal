package io.github.jiangbyte.voxel.boot.portal.domain.sys.banner;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * Banner 启用状态。
 *
 * Author: Charlie
 */
public enum BannerStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static BannerStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return BannerStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Banner status must be ENABLED or DISABLED");
        }
    }
}

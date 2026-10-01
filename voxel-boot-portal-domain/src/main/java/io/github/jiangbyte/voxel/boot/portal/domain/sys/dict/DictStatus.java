package io.github.jiangbyte.voxel.boot.portal.domain.sys.dict;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 字典项启用状态。
 *
 * Author: Charlie
 */
public enum DictStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static DictStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return DictStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Dict status must be ENABLED or DISABLED");
        }
    }
}

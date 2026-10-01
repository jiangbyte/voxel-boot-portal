package io.github.jiangbyte.voxel.boot.portal.domain.iam.org;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 组织启用状态。
 *
 * Author: Charlie
 */
public enum OrgStatus {

    ENABLED,
    DISABLED;

    /**
     * 解析状态字符串；空值默认 ENABLED。
     */
    public static OrgStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            return ENABLED;
        }
        try {
            return OrgStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Org status must be ENABLED or DISABLED");
        }
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

/**
 * 实名快照状态。
 *
 * Author: Charlie
 */
public enum IdentitySnapshotStatus {
    UNVERIFIED,
    VERIFIED,
    REVOKED;

    public static IdentitySnapshotStatus from(String value) {
        if (value == null || value.isBlank()) {
            return UNVERIFIED;
        }
        return IdentitySnapshotStatus.valueOf(value.trim().toUpperCase());
    }
}

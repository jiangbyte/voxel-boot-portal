package io.github.jiangbyte.voxel.boot.portal.domain.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;

/**
 * 账号类型：管理端 / 门户端。
 *
 * Author: Charlie
 */
public enum AccountType {

    ADMIN,
    PORTAL;

    /**
     * 解析账号类型；空值非法。
     */
    public static AccountType from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("Account type must not be blank");
        }
        try {
            return AccountType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Account type must be ADMIN or PORTAL");
        }
    }
}

package io.github.jiangbyte.voxel.boot.portal.domain.iam.account;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.Entity;

import java.util.UUID;

/**
 * 账号身份实体（非聚合根）：承载登录标识及其绑定状态。
 *
 * Author: Charlie
 */
public class AccountIdentity extends Entity<String> {

    private String id;
    private String identityType;
    private String identifier;
    private Integer verified;
    private Integer isPrimary;
    private String bindStatus;

    private AccountIdentity() {
    }

    /**
     * 新建已绑定身份。
     */
    public static AccountIdentity create(String identityType,
                                         String identifier,
                                         Integer verified,
                                         Integer primary) {
        // 1. 校验身份类型与标识非空
        String type = requireText(identityType, "Identity type must not be blank");
        String value = requireText(identifier, "Identifier must not be blank");
        // 2. 组装实体并分配标识
        AccountIdentity identity = new AccountIdentity();
        identity.id = UUID.randomUUID().toString().replace("-", "");
        identity.identityType = type.trim().toUpperCase();
        identity.identifier = value.trim();
        identity.verified = verified;
        identity.isPrimary = primary;
        identity.bindStatus = "BOUND";
        return identity;
    }

    /**
     * 从持久化状态重建（不触发副作用）。
     */
    public static AccountIdentity reconstitute(String id,
                                               String identityType,
                                               String identifier,
                                               Integer verified,
                                               Integer isPrimary,
                                               String bindStatus) {
        AccountIdentity identity = new AccountIdentity();
        identity.id = id;
        identity.identityType = identityType;
        identity.identifier = identifier;
        identity.verified = verified;
        identity.isPrimary = isPrimary;
        identity.bindStatus = bindStatus;
        return identity;
    }

    /**
     * 更新标识值（保持类型不变）。
     */
    public void changeIdentifier(String newIdentifier) {
        this.identifier = requireText(newIdentifier, "Identifier must not be blank").trim();
    }

    private static String requireText(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new DomainException(message);
        }
        return text;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getIdentityType() {
        return identityType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Integer getVerified() {
        return verified;
    }

    public Integer getIsPrimary() {
        return isPrimary;
    }

    public String getBindStatus() {
        return bindStatus;
    }
}

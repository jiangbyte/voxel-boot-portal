package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event.ProfileIdentityRevoked;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event.ProfileIdentityVerified;

import java.time.LocalDateTime;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;

/**
 * 账号实名认证快照聚合：主键为 accountId。
 *
 * Author: Charlie
 */
public class ProfileIdentity extends AggregateRoot<String> {

    private String accountId;
    private IdentitySnapshotStatus status;
    private String documentType;
    private String realNameCipher;
    private String documentNoCipher;
    private String documentNoHash;
    private String verifyChannel;
    private String provider;
    private String providerOrderNo;
    private LocalDateTime verifiedAt;
    private String sourceCaseId;
    private LocalDateTime revokedAt;
    private String revokedBy;

    private ProfileIdentity() {
    }

    /**
     * 审核通过后写入/覆盖实名快照。
     */
    public static ProfileIdentity verifyFromCase(String accountId,
                                                 String documentType,
                                                 String realNameCipher,
                                                 String documentNoCipher,
                                                 String documentNoHash,
                                                 String verifyChannel,
                                                 String provider,
                                                 String providerOrderNo,
                                                 String sourceCaseId) {
        // 1. 校验账号
        String id = requireAccountId(accountId);
        // 2. 组装 VERIFIED 快照
        ProfileIdentity identity = new ProfileIdentity();
        identity.accountId = id;
        identity.status = IdentitySnapshotStatus.VERIFIED;
        identity.documentType = blankToNull(documentType);
        identity.realNameCipher = blankToNull(realNameCipher);
        identity.documentNoCipher = blankToNull(documentNoCipher);
        identity.documentNoHash = blankToNull(documentNoHash);
        identity.verifyChannel = blankToNull(verifyChannel);
        identity.provider = blankToNull(provider);
        identity.providerOrderNo = blankToNull(providerOrderNo);
        identity.verifiedAt = LocalDateTime.now();
        identity.sourceCaseId = blankToNull(sourceCaseId);
        identity.revokedAt = null;
        identity.revokedBy = null;
        // 3. 登记认证事件
        identity.registerEvent(new ProfileIdentityVerified(id));
        return identity;
    }

    /**
     * 从持久化重建。
     */
    public static ProfileIdentity reconstitute(String accountId,
                                               String status,
                                               String documentType,
                                               String realNameCipher,
                                               String documentNoCipher,
                                               String documentNoHash,
                                               String verifyChannel,
                                               String provider,
                                               String providerOrderNo,
                                               LocalDateTime verifiedAt,
                                               String sourceCaseId,
                                               LocalDateTime revokedAt,
                                               String revokedBy) {
        ProfileIdentity identity = new ProfileIdentity();
        identity.accountId = accountId;
        identity.status = IdentitySnapshotStatus.from(status);
        identity.documentType = documentType;
        identity.realNameCipher = realNameCipher;
        identity.documentNoCipher = documentNoCipher;
        identity.documentNoHash = documentNoHash;
        identity.verifyChannel = verifyChannel;
        identity.provider = provider;
        identity.providerOrderNo = providerOrderNo;
        identity.verifiedAt = verifiedAt;
        identity.sourceCaseId = sourceCaseId;
        identity.revokedAt = revokedAt;
        identity.revokedBy = revokedBy;
        return identity;
    }

    /**
     * 在已有快照上覆盖为通过态（复用主键）。
     */
    public void applyApprovedCase(String documentType,
                                  String realNameCipher,
                                  String documentNoCipher,
                                  String documentNoHash,
                                  String verifyChannel,
                                  String provider,
                                  String providerOrderNo,
                                  String sourceCaseId) {
        // 1. 覆盖证件与通道字段
        this.status = IdentitySnapshotStatus.VERIFIED;
        this.documentType = blankToNull(documentType);
        this.realNameCipher = blankToNull(realNameCipher);
        this.documentNoCipher = blankToNull(documentNoCipher);
        this.documentNoHash = blankToNull(documentNoHash);
        this.verifyChannel = blankToNull(verifyChannel);
        this.provider = blankToNull(provider);
        this.providerOrderNo = blankToNull(providerOrderNo);
        this.verifiedAt = LocalDateTime.now();
        this.sourceCaseId = blankToNull(sourceCaseId);
        this.revokedAt = null;
        this.revokedBy = null;
        // 2. 登记事件
        registerEvent(new ProfileIdentityVerified(this.accountId));
    }

    /**
     * 撤销已认证快照。
     */
    public void revoke(String operatorId) {
        // 1. 仅 VERIFIED 可撤销
        if (this.status != IdentitySnapshotStatus.VERIFIED) {
            throw new DomainException(404, "Verified identity not found");
        }
        // 2. 写入撤销态
        this.status = IdentitySnapshotStatus.REVOKED;
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = blankToNull(operatorId);
        // 3. 登记事件
        registerEvent(new ProfileIdentityRevoked(this.accountId));
    }

    private static String requireAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new DomainException("Account id must not be blank");
        }
        return accountId.trim();
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    @Override
    public String getId() {
        return accountId;
    }

    public String getAccountId() { return accountId; }
    public IdentitySnapshotStatus getStatus() { return status; }
    public String getDocumentType() { return documentType; }
    public String getRealNameCipher() { return realNameCipher; }
    public String getDocumentNoCipher() { return documentNoCipher; }
    public String getDocumentNoHash() { return documentNoHash; }
    public String getVerifyChannel() { return verifyChannel; }
    public String getProvider() { return provider; }
    public String getProviderOrderNo() { return providerOrderNo; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public String getSourceCaseId() { return sourceCaseId; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public String getRevokedBy() { return revokedBy; }
}

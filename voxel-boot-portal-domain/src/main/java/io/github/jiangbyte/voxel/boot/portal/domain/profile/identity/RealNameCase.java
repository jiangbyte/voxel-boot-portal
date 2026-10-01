package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.AggregateRoot;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event.RealNameCaseDecided;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.event.RealNameCaseSubmitted;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import io.github.jiangbyte.voxel.boot.portal.domain.iam.account.Account;

/**
 * 实名业务工单聚合：维护提交、三方初始化、审核通过/驳回状态机。
 *
 * Author: Charlie
 */
public class RealNameCase extends AggregateRoot<String> {

    private String caseId;
    private String businessType;
    private String verifyChannel;
    private RealNameCaseStatus status;
    private String accountId;
    private String targetAccountHintCipher;
    private String applicantContactCipher;
    private String documentType;
    private String realNameCipher;
    private String documentNoCipher;
    private String documentNoHash;
    private final List<String> attachmentIds = new ArrayList<>();
    private String payloadCipher;
    private String handlerDeptId;
    private String provider;
    private String providerOrderNo;
    private String submitterId;
    private String reviewerId;
    private LocalDateTime reviewedAt;
    private String rejectReason;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;

    private RealNameCase() {
    }

    /**
     * 人工通道提交工单。
     */
    public static RealNameCase submitManual(String accountId,
                                            String businessType,
                                            String documentType,
                                            String realNameCipher,
                                            String documentNoCipher,
                                            String documentNoHash,
                                            List<String> attachmentIds,
                                            String applicantContactCipher) {
        // 1. 校验账号与证件密文
        String aid = requireText(accountId, "Account id must not be blank");
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            throw new DomainException("请上传证件材料");
        }
        // 2. 组装 PENDING/MANUAL 工单
        RealNameCase entity = newBlank(aid, businessType, VerifyChannel.MANUAL.name());
        entity.fillSensitive(documentType, realNameCipher, documentNoCipher, documentNoHash);
        entity.attachmentIds.addAll(attachmentIds.stream().filter(s -> s != null && !s.isBlank()).distinct().toList());
        entity.applicantContactCipher = blankToNull(applicantContactCipher);
        entity.submitterId = aid;
        // 3. 登记提交事件
        entity.registerEvent(new RealNameCaseSubmitted(entity.caseId));
        return entity;
    }

    /**
     * 三方通道初始化工单。
     */
    public static RealNameCase initThirdParty(String accountId,
                                              String businessType,
                                              String documentType,
                                              String realNameCipher,
                                              String documentNoCipher,
                                              String documentNoHash) {
        // 1. 校验账号
        String aid = requireText(accountId, "Account id must not be blank");
        // 2. 组装 PENDING/THIRD_PARTY
        RealNameCase entity = newBlank(aid, businessType, VerifyChannel.THIRD_PARTY.name());
        entity.fillSensitive(documentType, realNameCipher, documentNoCipher, documentNoHash);
        entity.submitterId = aid;
        // 3. 登记提交事件
        entity.registerEvent(new RealNameCaseSubmitted(entity.caseId));
        return entity;
    }

    /**
     * 从持久化重建。
     */
    public static RealNameCase reconstitute(String caseId,
                                            String businessType,
                                            String verifyChannel,
                                            String status,
                                            String accountId,
                                            String targetAccountHintCipher,
                                            String applicantContactCipher,
                                            String documentType,
                                            String realNameCipher,
                                            String documentNoCipher,
                                            String documentNoHash,
                                            List<String> attachmentIds,
                                            String payloadCipher,
                                            String handlerDeptId,
                                            String provider,
                                            String providerOrderNo,
                                            String submitterId,
                                            String reviewerId,
                                            LocalDateTime reviewedAt,
                                            String rejectReason,
                                            LocalDateTime expireAt,
                                            LocalDateTime createdAt) {
        RealNameCase entity = new RealNameCase();
        entity.caseId = caseId;
        entity.businessType = businessType;
        entity.verifyChannel = verifyChannel;
        entity.status = RealNameCaseStatus.from(status);
        entity.accountId = accountId;
        entity.targetAccountHintCipher = targetAccountHintCipher;
        entity.applicantContactCipher = applicantContactCipher;
        entity.documentType = documentType;
        entity.realNameCipher = realNameCipher;
        entity.documentNoCipher = documentNoCipher;
        entity.documentNoHash = documentNoHash;
        if (attachmentIds != null) {
            entity.attachmentIds.addAll(attachmentIds);
        }
        entity.payloadCipher = payloadCipher;
        entity.handlerDeptId = handlerDeptId;
        entity.provider = provider;
        entity.providerOrderNo = providerOrderNo;
        entity.submitterId = submitterId;
        entity.reviewerId = reviewerId;
        entity.reviewedAt = reviewedAt;
        entity.rejectReason = rejectReason;
        entity.expireAt = expireAt;
        entity.createdAt = createdAt;
        return entity;
    }

    /**
     * 绑定三方 provider 与订单号。
     */
    public void bindProvider(String provider, String providerOrderNo) {
        this.provider = blankToNull(provider);
        this.providerOrderNo = blankToNull(providerOrderNo);
    }

    /**
     * 审核通过（须 PENDING）。
     */
    public void approve(String reviewerId) {
        // 1. 状态门禁
        requirePending();
        // 2. 流转 APPROVED
        this.status = RealNameCaseStatus.APPROVED;
        this.reviewerId = blankToNull(reviewerId);
        this.reviewedAt = LocalDateTime.now();
        // 3. 登记事件
        registerEvent(new RealNameCaseDecided(this.caseId, this.status.name()));
    }

    /**
     * 审核驳回（须 PENDING）。
     */
    public void reject(String reviewerId, String reason) {
        // 1. 状态门禁
        requirePending();
        // 2. 流转 REJECTED
        this.status = RealNameCaseStatus.REJECTED;
        this.reviewerId = blankToNull(reviewerId);
        this.reviewedAt = LocalDateTime.now();
        this.rejectReason = requireText(reason, "Reject reason must not be blank").trim();
        // 3. 登记事件
        registerEvent(new RealNameCaseDecided(this.caseId, this.status.name()));
    }

    /**
     * 三方回调成功 → APPROVED。
     */
    public void approveByCallback(String message) {
        requirePending();
        this.status = RealNameCaseStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewerId = "SYSTEM";
        registerEvent(new RealNameCaseDecided(this.caseId, this.status.name()));
    }

    /**
     * 三方回调失败 → REJECTED。
     */
    public void rejectByCallback(String message) {
        requirePending();
        this.status = RealNameCaseStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewerId = "SYSTEM";
        this.rejectReason = (message == null || message.isBlank())
                ? "Third-party verification failed"
                : message.trim();
        registerEvent(new RealNameCaseDecided(this.caseId, this.status.name()));
    }

    private void requirePending() {
        if (this.status != RealNameCaseStatus.PENDING) {
            throw new DomainException("Case is not pending");
        }
    }

    private void fillSensitive(String documentType, String realNameCipher, String documentNoCipher, String documentNoHash) {
        this.documentType = documentType == null ? null : documentType.trim().toUpperCase(Locale.ROOT);
        this.realNameCipher = requireText(realNameCipher, "Real name cipher must not be blank");
        this.documentNoCipher = requireText(documentNoCipher, "Document no cipher must not be blank");
        this.documentNoHash = requireText(documentNoHash, "Document no hash must not be blank");
    }

    private static RealNameCase newBlank(String accountId, String businessType, String verifyChannel) {
        RealNameCase entity = new RealNameCase();
        entity.caseId = UUID.randomUUID().toString().replace("-", "");
        entity.businessType = RealNameBusinessType.from(businessType).name();
        entity.verifyChannel = verifyChannel;
        entity.status = RealNameCaseStatus.PENDING;
        entity.accountId = accountId;
        entity.createdAt = LocalDateTime.now();
        return entity;
    }

    private static String requireText(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new DomainException(message);
        }
        return text;
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    @Override
    public String getId() { return caseId; }
    public String getCaseId() { return caseId; }
    public String getBusinessType() { return businessType; }
    public String getVerifyChannel() { return verifyChannel; }
    public RealNameCaseStatus getStatus() { return status; }
    public String getAccountId() { return accountId; }
    public String getTargetAccountHintCipher() { return targetAccountHintCipher; }
    public String getApplicantContactCipher() { return applicantContactCipher; }
    public String getDocumentType() { return documentType; }
    public String getRealNameCipher() { return realNameCipher; }
    public String getDocumentNoCipher() { return documentNoCipher; }
    public String getDocumentNoHash() { return documentNoHash; }
    public List<String> getAttachmentIds() { return Collections.unmodifiableList(attachmentIds); }
    public String getPayloadCipher() { return payloadCipher; }
    public String getHandlerDeptId() { return handlerDeptId; }
    public String getProvider() { return provider; }
    public String getProviderOrderNo() { return providerOrderNo; }
    public String getSubmitterId() { return submitterId; }
    public String getReviewerId() { return reviewerId; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public String getRejectReason() { return rejectReason; }
    public LocalDateTime getExpireAt() { return expireAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

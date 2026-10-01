package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import io.github.jiangbyte.voxel.boot.portal.domain.common.adapter.port.PageResult;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileApi;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileInfo;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.IdentitySnapshotStatus;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.ProfileIdentity;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.ProfileIdentityRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameBusinessType;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCase;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.RealNameCaseRecord;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRecordRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.repository.RealNameCaseRepository;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.VerifyChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.IdentityCryptoPort;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.RealNameVerifyInitResult;
import io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port.RealNameVerifyPort;
import lombok.RequiredArgsConstructor;

/**
 * 实名工单用例：提交、三方初始化、回调、审核与分页。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class RealNameCaseImpl implements IRealNameCase {

    private static final List<String> DOCUMENT_TYPES = List.of("ID_CARD", "PASSPORT", "EID");

    private final RealNameCaseRepository realNameCaseRepository;
    private final RealNameCaseRecordRepository realNameCaseRecordRepository;
    private final ProfileIdentityRepository profileIdentityRepository;
    private final IProfileIdentityCase profileIdentityApplicationService;
    private final IdentityCryptoPort identityCryptoPort;
    private final RealNameVerifyPort realNameVerifyPort;
    private final FileApi fileApi;


    /**
     * 可选项：业务类型与证件类型。
     */
    public RealNameCaseOptionsView options() {
        RealNameCaseOptionsView.BusinessOption verify = new RealNameCaseOptionsView.BusinessOption(
                RealNameBusinessType.ACCOUNT_VERIFY.name(),
                "账号实名认证",
                List.of(VerifyChannel.MANUAL.name(), VerifyChannel.THIRD_PARTY.name()));
        return new RealNameCaseOptionsView(List.of(verify), new ArrayList<>(DOCUMENT_TYPES));
    }

    /**
     * 人工通道提交。
     */
    @Transactional
    public void submit(String accountId,
                       String businessType,
                       String documentType,
                       String realName,
                       String documentNo,
                       List<String> attachmentIds,
                       String applicantContact) {
        // 1. 业务校验：未认证、无 PENDING、证件未被占用
        String type = normalizeBusinessType(businessType);
        validateAccountVerifySubmit(accountId, documentType, documentNo);
        // 2. 加密敏感字段并创建 PENDING 工单
        String nameCipher = identityCryptoPort.encrypt(realName);
        String noCipher = identityCryptoPort.encrypt(documentNo);
        String noHash = identityCryptoPort.hashDocumentNo(documentType, documentNo);
        List<String> attachments = normalizeAttachmentIds(attachmentIds);
        String contactCipher = applicantContact == null || applicantContact.isBlank()
                ? null
                : identityCryptoPort.encrypt(applicantContact.trim());
        RealNameCase entity = RealNameCase.submitManual(
                accountId, type, documentType, nameCipher, noCipher, noHash, attachments, contactCipher);
        // 3. 落库并记流水
        realNameCaseRepository.save(entity);
        appendRecord(entity, "SUBMIT", null, entity.getStatus().name(), accountId, null);
    }

    /**
     * 三方通道初始化。
     */
    @Transactional
    public RealNameCaseInitView initThirdParty(String accountId,
                                               String businessType,
                                               String documentType,
                                               String realName,
                                               String documentNo,
                                               String provider) {
        // 1. 同人工校验
        String type = normalizeBusinessType(businessType);
        validateAccountVerifySubmit(accountId, documentType, documentNo);
        // 2. 创建 PENDING/THIRD_PARTY 工单
        String nameCipher = identityCryptoPort.encrypt(realName);
        String noCipher = identityCryptoPort.encrypt(documentNo);
        String noHash = identityCryptoPort.hashDocumentNo(documentType, documentNo);
        RealNameCase entity = RealNameCase.initThirdParty(
                accountId, type, documentType, nameCipher, noCipher, noHash);
        realNameCaseRepository.save(entity);
        // 3. 调用核验端口拿订单号
        if (!realNameVerifyPort.supports(VerifyChannel.THIRD_PARTY.name(), documentType, provider)) {
            throw new DomainException("不支持的实名核验提供方");
        }
        RealNameVerifyInitResult init = realNameVerifyPort.initVerify(
                entity.getCaseId(), documentType, realName, documentNo, provider);
        entity.bindProvider(init.provider(), init.providerOrderNo());
        realNameCaseRepository.save(entity);
        appendRecord(entity, "INIT_THIRD_PARTY", null, entity.getStatus().name(), accountId, null);
        // 4. 返回跳转信息
        return new RealNameCaseInitView(init.caseId(), init.provider(), init.providerOrderNo(), init.redirectUrl());
    }

    /**
     * 三方回调。
     */
    @Transactional
    public void callback(String caseId, String providerOrderNo, Integer success, String message) {
        // 1. 加载 PENDING 工单
        RealNameCase entity = requireCase(caseId);
        String before = entity.getStatus().name();
        realNameVerifyPort.handleCallback(caseId, providerOrderNo, success, message);
        // 2. 成功 → APPROVED + 写入快照；失败 → REJECTED
        if (java.util.Objects.equals(success, 1)) {
            entity.approveByCallback(message);
            realNameCaseRepository.save(entity);
            profileIdentityApplicationService.upsertOnApprove(entity);
            appendRecord(entity, "CALLBACK", before, entity.getStatus().name(), "SYSTEM", message);
            return;
        }
        entity.rejectByCallback(message);
        realNameCaseRepository.save(entity);
        appendRecord(entity, "CALLBACK", before, entity.getStatus().name(), "SYSTEM", entity.getRejectReason());
    }

    /**
     * 我的工单分页。
     */
    @Transactional(readOnly = true)
    public PageResult<RealNameCaseSummaryView> myPage(String accountId,
                                                      String businessType,
                                                      String status,
                                                      long current,
                                                      long size) {
        long page = Math.max(current, 1);
        long pageSize = Math.min(Math.max(size, 1), 100);
        long offset = (page - 1) * pageSize;
        long total = realNameCaseRepository.countByAccount(accountId, businessType, status);
        List<RealNameCaseSummaryView> records = realNameCaseRepository
                .pageByAccount(accountId, businessType, status, offset, pageSize)
                .stream()
                .map(this::toUserSummary)
                .toList();
        return new PageResult<>(records, total, page, pageSize);
    }

    /**
     * 审核分页。
     */
    @Transactional(readOnly = true)
    public PageResult<RealNameCaseSummaryView> reviewPage(String businessType,
                                                          String status,
                                                          String accountId,
                                                          long current,
                                                          long size) {
        String type = businessType == null || businessType.isBlank()
                ? RealNameBusinessType.ACCOUNT_VERIFY.name()
                : businessType.trim().toUpperCase(Locale.ROOT);
        long page = Math.max(current, 1);
        long pageSize = Math.min(Math.max(size, 1), 100);
        long offset = (page - 1) * pageSize;
        long total = realNameCaseRepository.countForReview(type, status, accountId);
        List<RealNameCaseSummaryView> records = realNameCaseRepository
                .pageForReview(type, status, accountId, offset, pageSize)
                .stream()
                .map(this::toSummary)
                .toList();
        return new PageResult<>(records, total, page, pageSize);
    }

    /**
     * 工单详情。
     */
    @Transactional(readOnly = true)
    public RealNameCaseDetailView detail(String caseId) {
        RealNameCase entity = requireCase(caseId);
        RealNameCaseSummaryView summary = toSummary(entity);
        return new RealNameCaseDetailView(
                summary.caseId(), summary.accountId(), summary.businessType(), summary.verifyChannel(),
                summary.status(), summary.documentType(), summary.realNameMasked(), summary.documentNoMasked(),
                summary.createdAt(), summary.reviewedAt(), summary.rejectReason(),
                entity.getProvider(), entity.getProviderOrderNo(), entity.getSubmitterId(), entity.getReviewerId(),
                resolveAttachments(entity.getAttachmentIds()));
    }

    /**
     * 人工审核通过。
     */
    @Transactional
    public void approve(String caseId, String reviewerId, String remark) {
        RealNameCase entity = requireCase(caseId);
        String before = entity.getStatus().name();
        entity.approve(reviewerId);
        realNameCaseRepository.save(entity);
        profileIdentityApplicationService.upsertOnApprove(entity);
        appendRecord(entity, "APPROVE", before, entity.getStatus().name(), reviewerId, remark);
    }

    /**
     * 人工审核驳回。
     */
    @Transactional
    public void reject(String caseId, String reviewerId, String rejectReason) {
        RealNameCase entity = requireCase(caseId);
        String before = entity.getStatus().name();
        entity.reject(reviewerId, rejectReason);
        realNameCaseRepository.save(entity);
        appendRecord(entity, "REJECT", before, entity.getStatus().name(), reviewerId, entity.getRejectReason());
    }

    private void validateAccountVerifySubmit(String accountId, String documentType, String documentNo) {
        // 1. 已认证禁止再提交
        ProfileIdentity identity = profileIdentityRepository.findById(accountId).orElse(null);
        if (identity != null && identity.getStatus() == IdentitySnapshotStatus.VERIFIED) {
            throw new DomainException("账号已完成实名认证");
        }
        // 2. 已有 PENDING
        if (realNameCaseRepository.countPendingByAccount(accountId, RealNameBusinessType.ACCOUNT_VERIFY.name()) > 0) {
            throw new DomainException("已有进行中的实名认证申请");
        }
        // 3. 证件唯一性
        if (documentNo == null || documentNo.isBlank()) {
            throw new DomainException("证件号码不能为空");
        }
        String hash = identityCryptoPort.hashDocumentNo(documentType, documentNo);
        profileIdentityRepository.findVerifiedByDocumentHash(hash, accountId).ifPresent(bound -> {
            throw new DomainException("该证件已被其他账号绑定");
        });
        realNameCaseRepository.findPendingByDocumentHash(hash, accountId).ifPresent(pending -> {
            throw new DomainException("该证件已有进行中的认证申请");
        });
    }

    private RealNameCase requireCase(String caseId) {
        return realNameCaseRepository.findById(caseId)
                .orElseThrow(() -> new DomainException(404, "Real-name case not found"));
    }

    private static String normalizeBusinessType(String businessType) {
        return RealNameBusinessType.from(businessType).name();
    }

    private List<String> normalizeAttachmentIds(List<String> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return List.of();
        }
        return attachmentIds.stream()
                .filter(id -> id != null && !id.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private List<RealNameCaseAttachmentView> resolveAttachments(List<String> attachmentIds) {
        List<RealNameCaseAttachmentView> attachments = new ArrayList<>();
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return attachments;
        }
        for (String objectName : attachmentIds) {
            FileInfo file = fileApi.getByObjectName(objectName).orElse(null);
            if (file == null) {
                attachments.add(new RealNameCaseAttachmentView(objectName, null, null, null, null, objectName));
            } else {
                attachments.add(new RealNameCaseAttachmentView(
                        file.objectName(), file.id(), file.originalName(), file.contentType(), file.size(),
                        file.url() == null ? file.objectName() : file.url()));
            }
        }
        return attachments;
    }

    private RealNameCaseSummaryView toSummary(RealNameCase entity) {
        String realNameMasked = entity.getRealNameCipher() == null ? null
                : identityCryptoPort.maskRealName(identityCryptoPort.decrypt(entity.getRealNameCipher()));
        String documentNoMasked = entity.getDocumentNoCipher() == null ? null
                : identityCryptoPort.maskDocumentNo(identityCryptoPort.decrypt(entity.getDocumentNoCipher()));
        return new RealNameCaseSummaryView(
                entity.getCaseId(),
                entity.getAccountId(),
                entity.getBusinessType(),
                entity.getVerifyChannel(),
                entity.getStatus().name(),
                entity.getDocumentType(),
                realNameMasked,
                documentNoMasked,
                entity.getCreatedAt(),
                entity.getReviewedAt(),
                entity.getRejectReason());
    }

    private RealNameCaseSummaryView toUserSummary(RealNameCase entity) {
        RealNameCaseSummaryView summary = toSummary(entity);
        return new RealNameCaseSummaryView(
                summary.caseId(), summary.accountId(), summary.businessType(), null,
                summary.status(), summary.documentType(), null, null,
                summary.createdAt(), summary.reviewedAt(), summary.rejectReason());
    }

    private void appendRecord(RealNameCase entity,
                              String action,
                              String statusBefore,
                              String statusAfter,
                              String operatorId,
                              String remark) {
        realNameCaseRecordRepository.append(
                RealNameCaseRecord.of(entity, action, statusBefore, statusAfter, operatorId, remark));
    }
}
